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

    public static void updateAccountDocument(String key, JsonObject body) throws MessagingException {
        getDb("resources-accounts").publish(ArangoDbRequest.upsert(key, body, body));
    }

    public static String saveTimelineEvent(JsonObject body) throws MessagingException {
        String key = UUID.randomUUID().toString();
        getDb("timeline-events").publish(ArangoDbRequest.insert(key, body));
        return key;
    }
}
