package com.acuitybotting.db.arango.acuity.rabbit_db.service;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.GsonRabbitDocument;
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

    public static Map<String, Object> buildQueryMap(String principalId, String database, String group, String key, String rev) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("principalId", principalId);
        queryMap.put("database", database);
        queryMap.put("subGroup", group);
        if (key != null) queryMap.put("subKey", key);
        if (rev != null) queryMap.put("_rev", rev);
        return queryMap;
    }

    public boolean isDeleteAccessible(String userId, String db) {
        if (db == null) return false;
        if (db.equals("services.registered-connections")) return false;
        return db.startsWith("services.") || db.startsWith("user.db.");
    }

    public boolean isWriteAccessible(String userId, String db) {
        if (db == null) return false;
        return db.startsWith("services.") || db.startsWith("user.db.");
    }

    public boolean isReadAccessible(String userId, String db) {
        if (db == null) return false;
        return db.startsWith("services.") || db.startsWith("user.db.");
    }

    public void save(int strategyType, Map<String, Object> queryMap, Map<String, Object> headers, String updateDocumentJson, String insertDocumentJson) {
        if (headers == null) headers = new HashMap<>();

        GsonRabbitDocument gsonRabbitDocument = new GsonRabbitDocument();
        gsonRabbitDocument.setPrincipalId((String) queryMap.get("principalId"));
        gsonRabbitDocument.setDatabase((String) queryMap.get("database"));
        gsonRabbitDocument.setSubGroup((String) queryMap.get("subGroup"));
        gsonRabbitDocument.setSubKey((String) queryMap.get("subKey"));
        gsonRabbitDocument.setHeaders(headers);
        headers.put("_lastUpdateTime", System.currentTimeMillis());

        gsonRabbitDocument.setSubDocument(gson.fromJson(updateDocumentJson, JsonElement.class));
        String updateDocument = gson.toJson(gsonRabbitDocument);

        headers.put("_insertTime", System.currentTimeMillis());
        gsonRabbitDocument.setSubDocument(gson.fromJson(insertDocumentJson, JsonElement.class));
        String insertDocument = gson.toJson(gsonRabbitDocument);

        String strategy = strategyType == 0 ? "REPLACE" : "UPDATE";
        String query = "UPSERT " + gson.toJson(queryMap) + " INSERT " + insertDocument + " " + strategy + " " + updateDocument + " IN " + COLLECTION;

        arangoOperations.query(query, null, null, null);
    }

    public void delete(Map<String, Object> queryMap) {
        String query = "REMOVE " + gson.toJson(queryMap) + "in " + COLLECTION;
        arangoOperations.query(query, null, null, null);
    }

    public GsonRabbitDocument loadByKey(Map<String, Object> queryMap) {
        String query = "FOR u IN " + COLLECTION + " FILTER u.principalId == @principalId && u.database == @database && u.subGroup == @subGroup && u.subKey == @subKey RETURN u";
        List<String> json = arangoOperations.query(query, queryMap, null, String.class).asListRemaining();
        if (json.size() > 0) return gson.fromJson(json.get(0), GsonRabbitDocument.class);
        return null;
    }

    public Set<GsonRabbitDocument> loadByGroup(Map<String, Object> queryMap) {
        String query = "FOR u IN " + COLLECTION + " FILTER u.principalId == @principalId && u.database == @database && u.subGroup == @subGroup RETURN u";
        List<String> json = arangoOperations.query(query, queryMap, null, String.class).asListRemaining();
        return json.stream().map(s -> gson.fromJson(s, GsonRabbitDocument.class)).collect(Collectors.toSet());
    }
}
