package com.acuitybotting.data.flow.messaging.services.db.implementations.rabbit;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.channel.RabbitChannel;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.queue.RabbitQueue;
import com.acuitybotting.data.flow.messaging.services.db.MessagingDb;
import com.acuitybotting.data.flow.messaging.services.db.domain.Document;
import com.acuitybotting.data.flow.messaging.services.db.domain.RabbitDbRequest;
import com.google.gson.Gson;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * Created by Zachary Herridge on 7/26/2018.
 */
public class RabbitDb implements MessagingDb {

    private Supplier<RabbitQueue> queueSupplier;
    private String exchange;
    private String route;

    private String database;

    private Gson gson = new Gson();

    public RabbitDb(String database, String exchange, String route, Supplier<RabbitQueue> queueSupplier) {
        this.database = database;
        this.exchange = exchange;
        this.route = route;
        this.queueSupplier = queueSupplier;
    }

    private String getRoute(RabbitDbRequest request) {
        return route +
                String.valueOf(request.getDatabase()) + "." +
                String.valueOf(request.getGroup()) + "." +
                String.valueOf(request.getKey());
    }

    public void deleteByKey(String documentGroup, String key) throws MessagingException {
        RabbitDbRequest delete =
                new RabbitDbRequest()
                        .setType(RabbitDbRequest.DELETE_BY_KEY)
                        .setGroup(documentGroup)
                        .setKey(key);
        send(delete);
    }

    public void upsert(String documentGroup, String key, String insertDocument, String updateDocument) throws MessagingException {
        upsert(documentGroup, key, null, insertDocument, updateDocument);
    }

    public void upsert(String documentGroup, String key, String rev, String insertDocument, String updateDocument) throws MessagingException {
        RabbitDbRequest upsert =
                new RabbitDbRequest()
                        .setType(RabbitDbRequest.UPSERT)
                        .setGroup(documentGroup)
                        .setKey(key)
                        .setRev(rev)
                        .setInsertDocument(insertDocument)
                        .setUpdateDocument(updateDocument);
        send(upsert);
    }

    public Document[] findAllByGroup(String documentGroup) throws MessagingException {
        RabbitDbRequest load =
                new RabbitDbRequest()
                        .setGroup(documentGroup)
                        .setType(RabbitDbRequest.FIND_BY_GROUP);

        String response = sendWithResponse(load);

        return gson.fromJson(response, Document[].class);
    }

    public Document findByGroupAndKey(String documentGroup, String key) throws MessagingException {
        RabbitDbRequest load =
                new RabbitDbRequest()
                        .setGroup(documentGroup)
                        .setType(RabbitDbRequest.FIND_BY_KEY)
                        .setKey(key);

        String response = sendWithResponse(load);
        if (response == null || response.isEmpty()) return null;
        return gson.fromJson(response, Document.class);
    }

    public void send(RabbitDbRequest request) throws MessagingException {
        RabbitChannel channel = Optional.ofNullable(queueSupplier.get()).map(RabbitQueue::getChannel).orElse(null);
        if (channel == null) throw new MessagingException("Not connected to RabbitMQ.");
        request.setDatabase(database);
        channel.buildMessage(
                exchange,
                getRoute(request),
                gson.toJson(request)).send();
    }

    public String sendWithResponse(RabbitDbRequest request) throws MessagingException {
        String queue = Optional.ofNullable(queueSupplier.get()).map(RabbitQueue::getName).orElse(null);
        RabbitChannel channel = Optional.ofNullable(queueSupplier.get()).map(RabbitQueue::getChannel).orElse(null);
        if (channel == null || queue == null) throw new MessagingException("Not connected to RabbitMQ.");
        request.setDatabase(database);
        try {
            Message message = channel.buildMessage(
                    exchange,
                    getRoute(request),
                    queue,
                    gson.toJson(request))
                    .send()
                    .get(10, TimeUnit.SECONDS).getMessage();
            return message.getBody();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new MessagingException("Failed to findByGroupAndKey document", e);
        }
    }
}
