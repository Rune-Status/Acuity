package com.acuitybotting.data.flow.messaging.services.client.utils;

import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.MessagingQueue;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitChannel;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.db.implementations.rabbit.RabbitDb;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Base64;
import java.util.Objects;
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

    private MessagingQueue localQueue;

    private RabbitClient rabbitClient;

    public void auth(String username, String password){
        this.username = username;
        this.password = password;
    }

    public void start(String connectionPrefix){
        start(connectionPrefix, UUID.randomUUID().toString());
    }

    public void start(String connectionPrefix, String connectionId){
        allowedPrefix = "user." + username + ".";
        this.connectionId = connectionPrefix + "_" + connectionId;

        rabbitClient = new RabbitClient();
        rabbitClient.auth("nodes-1.admin-acuitybotting.com", "31457", username, password);
        rabbitClient.connect(this.connectionId);
    }

    public void updateConnectionDocument(String body) throws MessagingException {
        getDb("services.registered-connections").upsert(
                "connections",
                getConnectionId(),
                RabbitDb.STRATEGY_UPDATE,
                body,
                body
        );
    }

    public RabbitChannelPool createPool(int size, Consumer<MessagingChannel> consumer){
        return new RabbitChannelPool(this, size, consumer);
    }

    public String getGeneralExchange(){
        return "acuitybotting.general";
    }

    public RabbitDb getDb(String db){
        return new RabbitDb(db, getGeneralExchange(), getAllowedPrefix() + "services.rabbit-db.handleRequest.", () -> localQueue);
    }

    public MessagingQueue createLocalQueue(){
        localQueue = rabbitClient.openChannel().createQueue(getAllowedPrefix() + "queue." + getConnectionId(), true);
        return localQueue;
    }

    public MessagingQueue getLocalQueue() {
        return localQueue;
    }

    public RabbitClient getRabbitClient() {
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
