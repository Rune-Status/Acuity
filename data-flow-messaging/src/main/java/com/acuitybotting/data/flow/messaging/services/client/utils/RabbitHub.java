package com.acuitybotting.data.flow.messaging.services.client.utils;

import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.MessagingQueue;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.db.implementations.rabbit.RabbitDb;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.*;

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
    private Set<MessagingChannel> channelPool = new HashSet<>();

    public void auth(String connectionKey){
        Objects.requireNonNull(connectionKey, "Failed to acquire user connection key.");
        username = new Gson().fromJson(new String(Base64.getDecoder().decode(connectionKey)), JsonObject.class).get("principalId").getAsString();
        password = connectionKey;
    }

    public void auth(String username, String password){
        this.username = username;
        this.password = password;
    }

    public void start(String connectionPrefix, int poolSize){
        allowedPrefix = "user." + username + ".";
        connectionId = connectionPrefix + "_" + UUID.randomUUID().toString();

        rabbitClient = new RabbitClient();
        rabbitClient.auth("messaging.acuitybotting.com", "30672", username, password);
        rabbitClient.connect(connectionId);

        for (int i = 0; i < poolSize; i++) {
            channelPool.add(rabbitClient.openChannel());
        }
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

    public String getGeneralExchange(){
        return "acuitybotting.general";
    }

    public RabbitDb getDb(String db){
        return new RabbitDb(db, getGeneralExchange(), getAllowedPrefix() + "services.rabbit-db.handleRequest.", () -> localQueue);
    }

    public MessagingQueue createLocalQueue(boolean create){
        localQueue = getRandomChannel().createQueue(getAllowedPrefix() + "queue." + getConnectionId(), create);
        return localQueue;
    }

    public MessagingChannel getRandomChannel(){
        return channelPool.stream().findAny().orElse(null);
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
