package com.acuitybotting.db.arango.acuity.rabbit_db.service;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.RabbitDocumentBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Zachary Herridge on 8/21/2018.
 */
public class RabbitDbQueryBuilder {

    private RabbitDbService service;

    private String query;
    private Map<String, Object> queryMap = new HashMap<>();
    private Map<String, Boolean> options = new HashMap<>();

    public RabbitDbQueryBuilder(RabbitDbService service, String query) {
        this.service = service;
        this.query = query;
    }

    public RabbitDbQueryBuilder withParam(String key, Object value){
        if (key == null || value == null) return this;
        queryMap.put(key, value);
        return this;
    }

    public RabbitDbQueryBuilder withOption(String key, boolean state){
        options.put(key, state);
        return this;
    }

    public RabbitDbQueryBuilder withMatch(String database, String group){
        return withMatch(null, database, group, null, null);
    }

    public RabbitDbQueryBuilder withMatch(String database, String group, String key){
        return withMatch(null, database, group, key, null);
    }

    public RabbitDbQueryBuilder withMatch(RabbitDocumentBase document) {
        return withMatch(document.getPrincipalId(), document.getDatabase(), document.getSubGroup(), document.getSubKey(), document.get_rev());
    }

    public RabbitDbQueryBuilder withMatch(String userId, String database, String group, String key) {
        return withMatch(userId, database, group, key, null);
    }

    public RabbitDbQueryBuilder withMatch(String userId, String database, String group, String key, String rev) {
        return withPrincipal(userId).withDatabase(database).withGroup(group).withKey(key).withRev(rev);
    }

    public RabbitDbQueryBuilder withPrincipal(String principalId){
        return withParam("principalId", principalId);
    }

    public RabbitDbQueryBuilder withDatabase(String database){
        return withParam("database", database);
    }

    public RabbitDbQueryBuilder withGroup(String group){
        return withParam("subGroup", group);
    }

    public RabbitDbQueryBuilder withKey(String key){
        return withParam("subKey", key);
    }

    public RabbitDbQueryBuilder withRev(String rev){
        return withParam("_rev", rev);
    }

    public <T> Set<T> findAll(Class<T> type){
        return service.findByQuery(query, queryMap, type);
    }

    public <T> Optional<T> findOne(Class<T> type){
        return service.findByQuery(query, queryMap, type).stream().findAny();
    }

    public void delete(){
        service.delete(queryMap);
    }

    public UpsertResult upsert(String document){
        return upsert(null, document, document);
    }

    public UpsertResult upsert(String update, String insert){
        return upsert(null, update, insert);
    }

    public UpsertResult upsert(Map<String, Object> headers){
        return upsert(headers, null, null);
    }

    public UpsertResult upsert(Map<String, Object> headers, String update, String insert){
        return service.upsert(queryMap, headers, options, update, insert);
    }

    public void execute() {
        service.getArangoOperations().query(query, queryMap, null, null);
    }
}
