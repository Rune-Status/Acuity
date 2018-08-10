package com.acuitybotting.data.flow.messaging.services.client.utils;

import com.acuitybotting.common.utils.JwtUtil;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.MessagingQueue;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.db.implementations.rabbit.RabbitDb;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Zachary Herridge on 8/10/2018.
 */
public class RabbitHub {

    private String sub;
    private String allowedPrefix;
    private String connectionId;

    private MessagingQueue localQueue;

    private RabbitClient rabbitClient;
    private Set<MessagingChannel> channelPool = new HashSet<>();

    public void start(String connectionPrefix, String jwt, int poolSize){
        Objects.requireNonNull(jwt, "Failed to acquire user jwt.");
        sub = JwtUtil.decodeBody(jwt).get("sub").getAsString();
        allowedPrefix = "user." + sub + ".";
        connectionId = connectionPrefix + "_" + UUID.randomUUID().toString();

        rabbitClient = new RabbitClient();
        rabbitClient.auth("195.201.248.164", sub, jwt);
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

    public String getSub() {
        return sub;
    }

    public String getAllowedPrefix() {
        return allowedPrefix;
    }
}
