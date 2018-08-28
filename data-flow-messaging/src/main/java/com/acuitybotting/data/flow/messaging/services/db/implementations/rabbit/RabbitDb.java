package com.acuitybotting.data.flow.messaging.services.db.implementations.rabbit;

import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.queue.RabbitQueue;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Zachary Herridge on 7/26/2018.
 */
public class RabbitDb {

    private String db;
    private RabbitQueue queue;
    private String exchange;
    private String route;

    public RabbitDb(String db, RabbitQueue queue, String exchange, String route) {
        this.db = db;
        this.queue = queue;
        this.exchange = exchange;
        this.route = route;
    }

    public void publish(JsonObject jsonObject)  {
        jsonObject.addProperty("db", db);

        try {
            queue.getChannel().buildMessage(exchange, route, jsonObject.toString()).send();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public Optional<JsonElement> publishWithResponse(JsonObject jsonObject) {
        jsonObject.addProperty("db", db);

        try {
            MessageEvent messageEvent = queue.getChannel().buildMessage(exchange, route, queue.getName(), jsonObject.toString()).send().get(10, TimeUnit.SECONDS);
            return Optional.ofNullable(messageEvent.getMessage().getBody()).map(s -> new Gson().fromJson(s, JsonElement.class));
        } catch (InterruptedException | ExecutionException | TimeoutException | MessagingException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
