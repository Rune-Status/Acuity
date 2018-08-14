package com.acuitybotting.db.arango.acuity.rabbit_db.service;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.RabbitDocumentBase;
import com.arangodb.ArangoCursor;
import com.arangodb.springframework.core.ArangoOperations;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public static <T extends RabbitDocumentBase> Map<String, Object> buildQueryMap(T document) {
        return buildQueryMap(document.getPrincipalId(), document.getDatabase(), document.getSubGroup(), document.getSubKey(), document.get_rev());
    }

    public static Map<String, Object> buildQueryMap(String principalId, String database, String group, String key) {
        return buildQueryMap(principalId, database, group, key, null);
    }

    public static Map<String, Object> buildQueryMap(String principalId, String database, String group) {
        return buildQueryMap(principalId, database, group, null, null);
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

    public static Map<String, Object> buildQueryMapMultiPrincipal(Collection<String> principalIds, String database, String group) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("principalIds", principalIds);
        queryMap.put("database", database);
        queryMap.put("subGroup", group);
        return queryMap;
    }

    public static Map<String, Object> buildQueryMapNoPrincipal(String database, String group) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("database", database);
        queryMap.put("subGroup", group);
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

        if (insertDocument == null) insertDocument = "{}";
        if (updateDocument == null) insertDocument = "{}";


        String strategy = strategyType == 0 ? "REPLACE" : "UPDATE";
        String query = "UPSERT " + gson.toJson(queryMap) + " INSERT " + insertDocument + " " + strategy + " " + updateDocument + " IN " + COLLECTION;

        arangoOperations.query(query, null, null, null);
    }

    public void delete(Map<String, Object> queryMap) {
        String query = "REMOVE " + gson.toJson(queryMap) + "in " + COLLECTION;
        arangoOperations.query(query, null, null, null);
    }

    public <T> T loadByKey(Map<String, Object> queryMap, Class<T> type) {
        String query = "FOR u IN " + COLLECTION + " FILTER u.principalId == @principalId && u.database == @database && u.subGroup == @subGroup && u.subKey == @subKey RETURN u";
        ArangoCursor<String> result = arangoOperations.query(query, queryMap, null, String.class);
        if (result == null || !result.hasNext()) return null;
        return gson.fromJson(result.next(), type);
    }

    public <T> Set<T> loadByGroup(Map<String, Object> queryMap, Class<T> type) {
        String query = "FOR u IN " + COLLECTION + " FILTER u.principalId == @principalId && u.database == @database && u.subGroup == @subGroup RETURN u";
        List<String> json = arangoOperations.query(query, queryMap, null, String.class).asListRemaining();
        return json.stream().map(s -> gson.fromJson(s, type)).collect(Collectors.toSet());
    }
}
