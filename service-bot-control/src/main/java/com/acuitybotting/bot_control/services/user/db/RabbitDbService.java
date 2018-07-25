package com.acuitybotting.bot_control.services.user.db;

import com.acuitybotting.bot_control.domain.RabbitDbRequest;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.db.arango.acuity.bot_control.domain.RabbitDocument;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.RabbitDocumentRepository;
import com.arangodb.ArangoCursor;
import com.arangodb.springframework.core.ArangoOperations;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
@Service
@Slf4j
public class RabbitDbService {

    public static final String COLLECTION = "RabbitDocument";

    private final ArangoOperations arangoOperations;
    private final RabbitDocumentRepository repository;
    private final Gson gson = new Gson();

    public RabbitDbService(ArangoOperations operations, RabbitDocumentRepository repository) {
        this.arangoOperations = operations;
        this.repository = repository;
    }

    private boolean isDeleteAccessible(String userId, String db){
        if (db == null) return false;
        return "script-settings".equals(db) || db.startsWith("user.db.");
    }

    private boolean isWriteAccessible(String userId, String db){
        if (db == null) return false;
        return "registered-connections".equals(db) || "script-settings".equals(db) || db.startsWith("user.db.");
    }

    private boolean isReadAccessible(String userId, String db){
        if (db == null) return false;
        return "registered-connections".equals(db) || "script-settings".equals(db) || db.startsWith("user.db.");
    }
    
    public void save(String principalId, RabbitDbRequest request, Map<String, Object> headers) {
        RabbitDocument rabbitDocument = new RabbitDocument();
        rabbitDocument.setPrincipalId(principalId);
        rabbitDocument.setSubGroup(request.getGroup());
        rabbitDocument.setSubKey(request.getKey());
        rabbitDocument.setDatabase(request.getDatabase());
        rabbitDocument.setHeaders(headers);

        rabbitDocument.setSubDocument(gson.fromJson(request.getInsertDocument(), JsonElement.class));
        String insertDocument = gson.toJson(rabbitDocument);

        rabbitDocument.setSubDocument(gson.fromJson(request.getUpdateDocument(), JsonElement.class));
        String updateDocument = gson.toJson(rabbitDocument);

        String strategy = request.getType() == RabbitDbRequest.SAVE_REPLACE ? "REPLACE" : "UPDATE";
        String query = "UPSERT " + buildQuery(principalId, request, true) + " INSERT " + insertDocument + " " + strategy + " " + updateDocument + " IN " + COLLECTION;

        arangoOperations.query(query, null, null, null);

    }

    private String buildQuery(String principalId, RabbitDbRequest request, boolean includeKey){
        return gson.toJson(buildQueryMap(principalId, request, includeKey));
    }

    private Map<String, Object> buildQueryMap(String principalId, RabbitDbRequest request, boolean includeKey){
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

    public RabbitDocument loadByKey(String userId, RabbitDbRequest request) {
        String query = "FOR u IN " + COLLECTION + " FILTER u.principalId == @principalId && u.database == @database && u.subGroup == @subGroup && u.subKey == @subKey RETURN u";
        List<String> strings = arangoOperations.query(query, buildQueryMap(userId, request, true), null, String.class).asListRemaining();
        return null;
    }

    private Set<RabbitDocument> loadByGroup(String userId, RabbitDbRequest request) {
        Set<RabbitDocument> result;
        String documentQuery = request.getDocumentQuery();
        if (documentQuery == null){
            result = repository.findAllByPrincipalIdAndDatabaseAndSubGroup(userId, request.getDatabase(), request.getGroup());
        }
        else {
            result = repository.findAllByPrincipalIdAndDatabaseAndSubGroupAndSubDocumentMatchesRegex(userId, request.getDatabase(), request.getGroup(), documentQuery);
        }
        return result;
    }

    public void handle(MessageEvent messageEvent, RabbitDbRequest request, String userId) {
        log.info("Handling db request {} for user {}.", request, userId);

        if (isWriteAccessible(userId, request.getDatabase())){
            if (request.getType() == RabbitDbRequest.SAVE_REPLACE || request.getType() == RabbitDbRequest.SAVE_UPDATE) {
                if (isWriteAccessible(userId, request.getDatabase())) save(userId, request, null);
            } else if (request.getType() == RabbitDbRequest.DELETE_BY_KEY && isDeleteAccessible(userId, request.getDatabase())) {
                delete(userId, request);
            }
        }

        if (isReadAccessible(userId, request.getDatabase())){
            if (request.getType() == RabbitDbRequest.FIND_BY_KEY) {
                RabbitDocument rabbitDocument = loadByKey(userId, request);
                messageEvent.getChannel().respond(messageEvent.getMessage(), rabbitDocument == null ? "" : gson.toJson(rabbitDocument));
            } else if (request.getType() == RabbitDbRequest.FIND_BY_GROUP) {
                messageEvent.getChannel().respond(messageEvent.getMessage(), gson.toJson(loadByGroup(userId, request)));
            }
        }
    }
}
