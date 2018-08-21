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
import java.util.stream.Stream;

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

    public RabbitDbQueryBuilder queryByKey() {
        return query("FOR u IN @@collection FILTER u.principalId == @principalId && u.database == @database && u.subGroup == @subGroup && u.subKey == @subKey RETURN u");
    }

    public RabbitDbQueryBuilder queryByGroup() {
        return query("FOR u IN @@collection FILTER u.principalId == @principalId && u.database == @database && u.subGroup == @subGroup RETURN u");
    }

    public RabbitDbQueryBuilder query(){
        return query("");
    }

    public RabbitDbQueryBuilder query(String query){
        return new RabbitDbQueryBuilder(this, query).withParam("@@collection", COLLECTION);
    }

    public void upsert(Map<String, Object> queryMap, Map<String, Object> headers, String updateDocumentJson, String insertDocumentJson) {
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

        String query = "UPSERT " + gson.toJson(queryMap) + " INSERT " + insertDocument + " UPDATE " + updateDocument + " IN " + COLLECTION;
        arangoOperations.query(query, null, null, null);
    }

    public void delete(Map<String, Object> queryMap) {
        String query = "REMOVE " + gson.toJson(queryMap) + "in " + COLLECTION;
        arangoOperations.query(query, null, null, null);
    }

    @SuppressWarnings("unchecked")
    public <T> Set<T> findByQuery(String query, Map<String, Object> queryMap, Class<T> type) {
        List<String> json = arangoOperations.query(query, queryMap, null, String.class).asListRemaining();
        Stream<GsonRabbitDocument> stream = json.stream().map(s -> gson.fromJson(s, GsonRabbitDocument.class));
        if (!type.equals(GsonRabbitDocument.class)) stream.map(gsonRabbitDocument -> gsonRabbitDocument.getSubDocumentAs(type));
        return (Set<T>) stream.collect(Collectors.toSet());
    }
}
