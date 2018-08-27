package com.acuitybotting.client.bot.control.db;

import com.acuitybotting.client.bot.control.AcuityHub;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.db.domain.RabbitDbRequest;
import com.acuitybotting.data.flow.messaging.services.db.implementations.rabbit.RabbitDb;

import java.util.UUID;

public class RabbitDBHub {

    public static RabbitDb getDb(String db){
        return AcuityHub.getRabbitHub().getDb(db);
    }

    public static void updateAccountDocument(String email, String body) throws MessagingException {
        RabbitDbRequest upsert =
                new RabbitDbRequest()
                        .setType(RabbitDbRequest.UPSERT)
                        .setGroup("connections")
                        .setKey(email)
                        .setInsertDocument(body)
                        .setUpdateDocument(body);

        upsert.getOptions().put("mergeObjects", false);
        getDb("services.rs-accounts").send(upsert);
    }

    public static String saveTimelineEvent(String body) throws MessagingException {
        String key = UUID.randomUUID().toString();
        getDb("services.timeline-events").upsert(
                "events",
                key,
                body
        );
        return key;
    }
}
