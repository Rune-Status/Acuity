package com.acuitybotting.services.arangodb.rabbit.services;


import com.acuitybotting.common.utils.GsonUtil;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.db.arangodb.api.query.Aql;
import com.acuitybotting.db.arangodb.api.services.ArangoDbService;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class DbRequestHandlerService {

    private final ArangoDbService dbService;

    @Autowired
    public DbRequestHandlerService(ArangoDbService dbService) {
        this.dbService = dbService;
    }

    @EventListener
    public void handle(MessageEvent messageEvent){
        if (!messageEvent.getRouting().endsWith(".services.arangodb.request")) return;

        JsonObject jsonObject = GsonUtil.getGson().fromJson(messageEvent.getMessage().getBody(), JsonObject.class);
        System.out.println();

        String type = GsonUtil.getOrDefault(jsonObject.get("type"), null);
        String collection = GsonUtil.getOrDefault(jsonObject.get("db"), null);
        String key = GsonUtil.getOrDefault(jsonObject.get("key"), null);
        if (type == null || collection == null || key == null) return;


        if (type.equals("upsert")){
            JsonObject insert = jsonObject.getAsJsonObject("insert");
            insert.addProperty("_key", key);
            insert.addProperty("_insertedAt", System.currentTimeMillis());
            JsonObject update = jsonObject.getAsJsonObject("update");
            update.addProperty("_updatedAt", System.currentTimeMillis());
            dbService.execute(Aql.upsertByKey(key, insert, update).withParameters("@collection", collection, "key", key));
        }

    }
}
