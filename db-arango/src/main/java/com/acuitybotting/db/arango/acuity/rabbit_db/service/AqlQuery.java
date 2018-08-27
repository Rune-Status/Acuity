package com.acuitybotting.db.arango.acuity.rabbit_db.service;

import com.google.gson.Gson;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AqlQuery {

    private StringJoiner queryBuilder = new StringJoiner("\n");

    private Map<String, Object> queryParameters = new HashMap<>();
    private Map<String, Object> jsonReplacements = new HashMap<>();

    private Map<String, Boolean> options = new HashMap<>();

    private List<String> references = new ArrayList<>();

    public AqlQuery append(String query){
        queryBuilder.add(query);
        return this;
    }

    public AqlQuery withFilter(String comparison){
        return withFilter(comparison, true);
    }

    public AqlQuery withFilter(String comparison, boolean condition){
        if (condition) queryBuilder.add("FILTER " + comparison);
        return this;
    }

    public AqlQuery withParameter(String key, Object value){
        if (key.startsWith("$")) jsonReplacements.put(key, value);
        else queryParameters.put(key, value);
        return this;
    }

    public AqlQuery withReferences(String... values){
        Collections.addAll(references, values);
        return this;
    }

    public AqlQuery withReturn(String value){
        if (references.size() > 0){
            String collect = references.stream().map(s -> s  + ": DOCUMENT(" + value + "." + s +  ")").collect(Collectors.joining(", ", "{", "}"));
            queryBuilder.add("RETURN MERGE_RECURSIVE(" + value + ", " + collect + ")");
        }
        else {
            queryBuilder.add("RETURN " + value);
        }
        return this;
    }

    public String build(){
        String query = queryBuilder.toString();

        for (Map.Entry<String, Object> entry : jsonReplacements.entrySet()) {
            String json;
            if (entry.getValue() instanceof String) json = (String) entry.getValue();
            else json = new Gson().toJson(entry.getValue());
            query = query.replaceAll(Pattern.quote(entry.getKey()), json);
        }

        return query;
    }

    public void execute(){

    }

    public static void main(String[] args) {
        AqlQuery refTest = new AqlQuery();
        refTest.append("LET d = DOCUMENT('Test/user1')")
                .withReferences("inventory", "bank")
                .withReturn("d");


        AqlQuery upsert = new AqlQuery();
        upsert.append("UPSERT {_key : @key}")
                .append("INSERT $insert")
                .append("UPDATE $update")
                .append("IN @@collection")
                .append("RETURN {current: NEW, previous: OLD}")
                .withParameter("key", "user1")
                .withParameter("$insert", "{_key: @key, displayName: 'Zach', searches: 0}")
                .withParameter("$update", "{searches: OLD.searches + 1}");

        System.out.println(upsert.build());
    }
}
