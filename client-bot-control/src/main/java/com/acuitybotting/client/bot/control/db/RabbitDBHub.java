package com.acuitybotting.client.bot.control.db;

import com.acuitybotting.client.bot.control.AcuityHub;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.db.arangodb.ArangoDbRequest;
import com.acuitybotting.data.flow.messaging.services.db.implementations.rabbit.RabbitDb;
import com.google.gson.JsonObject;

import java.util.UUID;

public class RabbitDBHub {

    public static RabbitDb getDb(String db){
        return AcuityHub.getRabbitHub().getDb(db);
    }

    public static void updateAccountDocument(String key, JsonObject body) {
        getDb("resources-accounts").publish(ArangoDbRequest.upsert(key, body, body));
    }

    public static void updateAccountItemsDocument(String key, JsonObject body) {
        getDb("resources-accounts").publish(ArangoDbRequest.upsertReplace(key, body, body));
    }

    public static String saveTimelineEvent(String key, JsonObject body) {
        if (key == null) key = UUID.randomUUID().toString();
        getDb("timeline-events").publish(ArangoDbRequest.upsert(key, body, body));
        return key;
    }
}
