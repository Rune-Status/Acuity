package com.acuitybotting.db.arango.acuity.rabbit_db.service;

import java.util.*;
import java.util.stream.Collectors;

public class AqlQuery {

    private StringJoiner queryBuilder = new StringJoiner("\n");

    private Map<String, Object> params = new HashMap<>();
    private Map<String, Boolean> options = new HashMap<>();

    private List<String> refs = new ArrayList<>();

    public AqlQuery filter(String comparison){
        return filter(comparison, true);
    }

    public AqlQuery filter(String comparison, boolean condition){
        if (condition) queryBuilder.add("FILTER " + comparison);
        return this;
    }

    public AqlQuery append(String query){
        queryBuilder.add(query);
        return this;
    }

    public AqlQuery withParameter(String key, Object value){
        params.put(key, value);
        return this;
    }

    public AqlQuery withReferences(String... values){
        Collections.addAll(refs, values);
        return this;
    }

    public AqlQuery withReturn(String value){
        if (refs.size() > 0){
            String collect = refs.stream().map(s -> "{" + s  + ": DOCUMENT(" + value + "." + s +  ")}").collect(Collectors.joining(", "));
            queryBuilder.add("RETURN MERGE_RECURSIVE(" + value + ", " + collect + ")");
        }
        else {
            queryBuilder.add("RETURN " + value);
        }
        return this;
    }

    public String build(){
        return queryBuilder.toString();
    }


    public static void main(String[] args) {
        AqlQuery aqlQuery = new AqlQuery();

        aqlQuery.append("FOR d in @@collection")
                .filter("d.connected = true")
                .withReferences("inventory", "bank", "equipment")
                .withReturn("d");


        System.out.println(aqlQuery.build());
    }
}
