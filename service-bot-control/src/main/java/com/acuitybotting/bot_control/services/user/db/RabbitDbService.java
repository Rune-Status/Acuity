package com.acuitybotting.bot_control.services.user.db;

import com.acuitybotting.bot_control.domain.RabbitDbRequest;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.data.flow.messaging.services.identity.RoutingUtil;
import com.acuitybotting.db.arango.acuity.bot_control.domain.RabbitDocument;
import com.acuitybotting.db.arango.acuity.bot_control.domain.RegisteredConnection;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.RegisteredConnectionRepository;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.RabbitDocumentRepository;
import com.arangodb.model.AqlQueryOptions;
import com.arangodb.springframework.core.ArangoOperations;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
@Service
@Slf4j
public class RabbitDbService {

    public static final String CONNECTIONS_DATABASE = "registered-connections";
    private final ArangoOperations arangoOperations;
    private final RabbitDocumentRepository repository;
    private final RegisteredConnectionRepository registeredConnectionRepository;
    private final Gson gson = new Gson();

    public RabbitDbService(ArangoOperations operations, RabbitDocumentRepository repository, RegisteredConnectionRepository registeredConnectionRepository) {
        this.arangoOperations = operations;
        this.repository = repository;
        this.registeredConnectionRepository = registeredConnectionRepository;
    }

    private boolean isWriteAccessible(String userId, String db){
        if (db == null) return false;
        return "registered-connections".equals(db) || "script-settings".equals(db) || db.startsWith("user.db.");
    }

    private boolean isReadAccessible(String userId, String db){
        if (db == null) return false;
        return "registered-connections".equals(db) || "script-settings".equals(db) || db.startsWith("user.db.");
    }

    public void save(String userId, RabbitDbRequest request, Map<String, Object> headers) {
        RabbitDocument rabbitDocument = new RabbitDocument();
        rabbitDocument.setPrincipalId(userId);
        rabbitDocument.setSubGroup(request.getGroup());
        rabbitDocument.setSubKey(request.getKey());
        rabbitDocument.setDatabase(request.getDatabase());
        rabbitDocument.setSubDocument(request.getDocument());
        rabbitDocument.setHeaders(headers);

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("principalId", rabbitDocument.getPrincipalId());
        queryMap.put("database", rabbitDocument.getDatabase());
        queryMap.put("subGroup", rabbitDocument.getSubGroup());
        queryMap.put("subKey", rabbitDocument.getSubKey());
        if (request.getRev() != null) queryMap.put("_rev", request.getRev());

        String upsertQuery = gson.toJson(queryMap);
        String document = gson.toJson(rabbitDocument);

        String strategy = request.getType() == RabbitDbRequest.SAVE_REPLACE ? "REPLACE" : "UPDATE";
        String query = "UPSERT " + upsertQuery + " INSERT " + document + " " + strategy + " " + document + " IN RabbitDocument";

        log.info("Query: " + query);
        arangoOperations.query(query, null, new AqlQueryOptions().count(true), null);

    }

    private void delete(String userId, RabbitDbRequest request) {
        repository.deleteAllByPrincipalIdAndDatabaseAndSubGroupAndSubKey(userId, request.getDatabase(), request.getGroup(), request.getKey());
    }

    private RabbitDocument loadByKey(String userId, RabbitDbRequest request) {
        return repository.findByPrincipalIdAndDatabaseAndSubGroupAndSubKey(userId, request.getDatabase(), request.getGroup(), request.getKey()).orElse(null);
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

    private RabbitDocument connectionToUserDoc(RegisteredConnection registeredConnection) {
        RabbitDocument document = new RabbitDocument();
        document.setSubDocument(gson.toJson(registeredConnection));
        return document;
    }

    public void handle(MessageEvent messageEvent, RabbitDbRequest request, String userId) {
        log.info("Handling db request {} for user {}.", request, userId);

        if (isWriteAccessible(userId, request.getDatabase())){
            if (request.getType() == RabbitDbRequest.SAVE_REPLACE || request.getType() == RabbitDbRequest.SAVE_UPDATE) {
                if (isWriteAccessible(userId, request.getDatabase())) save(userId, request, null);
            } else if (request.getType() == RabbitDbRequest.DELETE_BY_KEY) {
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
