package com.acuitybotting.data.flow.messaging.services.db.implementations.rabbit;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.MessagingQueue;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
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

    public static final int STRATEGY_REPLACE = RabbitDbRequest.SAVE_REPLACE;
    public static final int STRATEGY_UPDATE = RabbitDbRequest.SAVE_UPDATE;

    private Supplier<MessagingQueue> queueSupplier;
    private String exchange;
    private String route;

    private String database;

    private Gson gson = new Gson();

    public RabbitDb(String database, String exchange, String route, Supplier<MessagingQueue> queueSupplier) {
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

    public void update(String documentGroup, String key, String document) throws MessagingException {
        update(documentGroup, key, null, document);
    }

    public void update(String documentGroup, String key, String rev, String document) throws MessagingException {
        upsert(documentGroup, key, rev, STRATEGY_UPDATE, document, document);
    }

    public void save(String documentGroup, String key, String document) throws MessagingException {
        save(documentGroup, key, null, document);
    }

    public void save(String documentGroup, String key, String rev, String document) throws MessagingException {
        upsert(documentGroup, key, rev, STRATEGY_REPLACE, document, document);
    }

    public void upsert(String documentGroup, String key, int strategy, String insertDocument, String updateDocument) throws MessagingException {
        upsert(documentGroup, key, null, strategy, insertDocument, updateDocument);
    }

    public void upsert(String documentGroup, String key, String rev, int strategy, String insertDocument, String updateDocument) throws MessagingException {
        RabbitDbRequest upsert =
                new RabbitDbRequest()
                        .setType(strategy)
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
        MessagingChannel channel = Optional.ofNullable(queueSupplier.get()).map(MessagingQueue::getChannel).orElse(null);
        if (channel == null) throw new MessagingException("Not connected to RabbitMQ.");
        request.setDatabase(database);
        channel.send(
                exchange,
                getRoute(request),
                gson.toJson(request));
    }

    public String sendWithResponse(RabbitDbRequest request) throws MessagingException {
        String queue = Optional.ofNullable(queueSupplier.get()).map(MessagingQueue::getName).orElse(null);
        MessagingChannel channel = Optional.ofNullable(queueSupplier.get()).map(MessagingQueue::getChannel).orElse(null);
        if (channel == null || queue == null) throw new MessagingException("Not connected to RabbitMQ.");
        request.setDatabase(database);
        try {
            Message message = channel.send(
                    exchange,
                    getRoute(request),
                    queue,
                    gson.toJson(request))
                    .get(10, TimeUnit.SECONDS).getMessage();
            return message.getBody();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new MessagingException("Failed to findByGroupAndKey document", e);
        }
    }
}
