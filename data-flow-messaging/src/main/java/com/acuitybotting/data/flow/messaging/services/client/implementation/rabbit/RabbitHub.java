package com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit;


import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.channel.RabbitChannel;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.channel.RabbitChannelPool;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.client.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.queue.RabbitQueue;
import com.acuitybotting.data.flow.messaging.services.db.domain.RabbitDbRequest;
import com.acuitybotting.data.flow.messaging.services.db.implementations.rabbit.RabbitDb;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 8/10/2018.
 */
public class RabbitHub {

    private String username;
    private String password;

    private String allowedPrefix;
    private String connectionId;

    private RabbitChannelPool localPool;
    private RabbitQueue localQueue;

    private RabbitClient rabbitClient;

    public void auth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void start() {
        start(UUID.randomUUID().toString().replaceAll("-", ""));
    }

    public void start(String connectionId) {
        allowedPrefix = "user." + username + ".";
        this.connectionId = connectionId;

        rabbitClient = new RabbitClient();
        rabbitClient.auth("nodes-1.admin-acuitybotting.com", "31457", username, password);
        rabbitClient.connect(this.connectionId );

        localPool = createPool(2, null);
        localQueue = localPool
                .createQueue(getAllowedPrefix() + "queue." + getConnectionId(), true)
                .open(true);
    }

    public void updateConnectionDocument(String body) throws MessagingException {
        getDb("services.registered-connections").upsert(
                "connections",
                getConnectionId(),
                body
        );
    }

    public RabbitChannelPool createPool(int size) {
        return createPool(size, null);
    }

    public RabbitChannelPool createPool(int size, Consumer<RabbitChannel> consumer) {
        return new RabbitChannelPool(this, size, consumer);
    }

    public RabbitChannelPool getLocalPool() {
        return localPool;
    }

    public String getGeneralExchange() {
        return "acuitybotting.general";
    }

    public RabbitDb getDb(String db) {
        return new RabbitDb(db, getGeneralExchange(), getAllowedPrefix() + "services.rabbit-db.handleRequest.", () -> localQueue);
    }

    public RabbitQueue getLocalQueue() {
        return localQueue;
    }

    public RabbitClient getClient() {
        return rabbitClient;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public String getUsername() {
        return username;
    }

    public String getAllowedPrefix() {
        return allowedPrefix;
    }
}
