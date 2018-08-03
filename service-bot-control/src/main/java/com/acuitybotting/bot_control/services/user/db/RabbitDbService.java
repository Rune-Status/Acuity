package com.acuitybotting.bot_control.services.user.db;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.JsonRabbitDocument;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.db.domain.RabbitDbRequest;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;

import com.arangodb.springframework.core.ArangoOperations;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
@Service
@Getter
@Slf4j
public class RabbitDbService {

    public static final String COLLECTION = "RabbitDocument";

    private final ArangoOperations arangoOperations;
    private final Gson gson = new Gson();

    public RabbitDbService(ArangoOperations operations) {
        this.arangoOperations = operations;
    }

    private boolean isDeleteAccessible(String userId, String db) {
        if (db == null) return false;
        if (db.equals("services.registered-connections")) return false;
        return db.startsWith("services.") || db.startsWith("user.db.");
    }

    private boolean isWriteAccessible(String userId, String db) {
        if (db == null) return false;
        return db.startsWith("services.") || db.startsWith("user.db.");
    }

    private boolean isReadAccessible(String userId, String db) {
        if (db == null) return false;
        return db.startsWith("services.") || db.startsWith("user.db.");
    }

    public void save(String principalId, RabbitDbRequest request, Map<String, Object> headers) {
        JsonRabbitDocument jsonRabbitDocument = new JsonRabbitDocument();
        jsonRabbitDocument.setPrincipalId(principalId);
        jsonRabbitDocument.setSubGroup(request.getGroup());
        jsonRabbitDocument.setSubKey(request.getKey());
        jsonRabbitDocument.setDatabase(request.getDatabase());
        jsonRabbitDocument.setHeaders(headers);

        jsonRabbitDocument.setSubDocument(gson.fromJson(request.getInsertDocument(), JsonElement.class));
        String insertDocument = gson.toJson(jsonRabbitDocument);

        jsonRabbitDocument.setSubDocument(gson.fromJson(request.getUpdateDocument(), JsonElement.class));
        String updateDocument = gson.toJson(jsonRabbitDocument);

        String strategy = request.getType() == RabbitDbRequest.SAVE_REPLACE ? "REPLACE" : "UPDATE";
        String query = "UPSERT " + buildQuery(principalId, request, true) + " INSERT " + insertDocument + " " + strategy + " " + updateDocument + " IN " + COLLECTION;

        arangoOperations.query(query, null, null, null);
    }

    private String buildQuery(String principalId, RabbitDbRequest request, boolean includeKey) {
        return gson.toJson(buildQueryMap(principalId, request, includeKey));
    }

    private Map<String, Object> buildQueryMap(String principalId, RabbitDbRequest request, boolean includeKey) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("principalId", principalId);
        queryMap.put("database", request.getDatabase());
        queryMap.put("subGroup", request.getGroup());
        if (includeKey && request.getKey() != null) queryMap.put("subKey", request.getKey());
        if (request.getRev() != null) queryMap.put("_rev", request.getRev());
        return queryMap;
    }

    private void delete(String userId, RabbitDbRequest request) {
        String query = "REMOVE " + buildQuery(userId, request, true) + "in " + COLLECTION;
        arangoOperations.query(query, null, null, null);
    }

    public JsonRabbitDocument loadByKey(String userId, RabbitDbRequest request) {
        String query = "FOR u IN " + COLLECTION + " FILTER u.principalId == @principalId && u.database == @database && u.subGroup == @subGroup && u.subKey == @subKey RETURN u";
        List<String> json = arangoOperations.query(query, buildQueryMap(userId, request, true), null, String.class).asListRemaining();
        if (json.size() > 0) return gson.fromJson(json.get(0), JsonRabbitDocument.class);
        return null;
    }

    public Set<JsonRabbitDocument> loadByGroup(String userId, RabbitDbRequest request) {
        String query = "FOR u IN " + COLLECTION + " FILTER u.principalId == @principalId && u.database == @database && u.subGroup == @subGroup RETURN u";
        List<String> json = arangoOperations.query(query, buildQueryMap(userId, request, false), null, String.class).asListRemaining();
        return json.stream().map(s -> gson.fromJson(s, JsonRabbitDocument.class)).collect(Collectors.toSet());
    }

    public void handle(MessageEvent messageEvent, RabbitDbRequest request, String userId) {
        log.info("Handling db request {} for user {}.", request, userId);

        if (isWriteAccessible(userId, request.getDatabase())) {
            if (request.getType() == RabbitDbRequest.SAVE_REPLACE || request.getType() == RabbitDbRequest.SAVE_UPDATE) {
                if (isWriteAccessible(userId, request.getDatabase())) save(userId, request, null);
            } else if (request.getType() == RabbitDbRequest.DELETE_BY_KEY && isDeleteAccessible(userId, request.getDatabase())) {
                delete(userId, request);
            }
        }

        try {
            if (isReadAccessible(userId, request.getDatabase())) {
                if (request.getType() == RabbitDbRequest.FIND_BY_KEY) {
                    JsonRabbitDocument jsonRabbitDocument = loadByKey(userId, request);
                    try {
                        messageEvent.getQueue().getChannel().respond(messageEvent.getMessage(), jsonRabbitDocument == null ? "" : gson.toJson(jsonRabbitDocument));
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                } else if (request.getType() == RabbitDbRequest.FIND_BY_GROUP) {
                    messageEvent.getQueue().getChannel().respond(messageEvent.getMessage(), gson.toJson(loadByGroup(userId, request)));
                }
            }
        } catch (MessagingException e) {
            log.error("Error during response", e);
        }
    }
}
