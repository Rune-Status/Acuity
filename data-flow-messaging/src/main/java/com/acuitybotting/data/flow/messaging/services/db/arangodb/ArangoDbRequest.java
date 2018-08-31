package com.acuitybotting.data.flow.messaging.services.db.arangodb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ArangoDbRequest {

    private static final Gson gson = new Gson();

    public static JsonObject findByKey(String key){
        return request("findByKey", key);
    }

    public static JsonObject insert(String key, Object document){
        JsonObject insert = request("insert", key);
        insert.add("document", gson.toJsonTree(document));
        return insert;
    }

    public static JsonObject upsert(String key, Object update, Object insert){
        JsonObject upsert = request("upsert", key);
        upsert.add("update", gson.toJsonTree(update));
        upsert.add("insert", gson.toJsonTree(insert));
        return upsert;
    }

    public static JsonObject upsertReplace(String key, Object update, Object insert){
        JsonObject upsert = upsert(key, update, insert);
        upsert.addProperty("replace", true);
        return upsert;
    }

    private static JsonObject request(String type, String key){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", type);
        jsonObject.addProperty("key", key);
        return jsonObject;
    }
}
