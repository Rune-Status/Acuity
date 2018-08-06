package com.acuitybotting.bot.launcher.services;

import com.acuitybotting.common.utils.JwtUtil;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitQueue;
import com.acuitybotting.data.flow.messaging.services.db.domain.Document;
import com.acuitybotting.data.flow.messaging.services.db.implementations.rabbit.RabbitDb;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

/**
 * Created by Zachary Herridge on 8/6/2018.
 */
@Service
@Slf4j
public class LauncherRabbitService implements CommandLineRunner {

    private final RSPeerService rsPeerService;

    private MessagingChannel channel;

    private String sub;
    private String allowedPrefix;
    private RabbitQueue localQueue;
    private RabbitDb connectionsDb;

    private Gson gson = new Gson();

    @Autowired
    public LauncherRabbitService(RSPeerService rsPeerService) {
        this.rsPeerService = rsPeerService;
    }

    private void connect() {
        try {
            String jwt = rsPeerService.getSession();
            Objects.requireNonNull(jwt, "Failed to acquire user jwt.");
            sub = JwtUtil.decodeBody(jwt).get("sub").getAsString();
            allowedPrefix = "user." + sub + ".";

            RabbitClient rabbitClient = new RabbitClient();
            rabbitClient.auth("195.201.248.164", sub, jwt);
            rabbitClient.connect("ABL_" + UUID.randomUUID().toString());
            channel = rabbitClient.openChannel();
            localQueue = channel.createQueue(allowedPrefix + "queue." + rabbitClient.getRabbitId(), true)
                    .withListener(this::handleMessage)
                    .open(true);

            connectionsDb = new RabbitDb("services.registered-connections", "acuitybotting.general", allowedPrefix + "services.rabbit-db.handleRequest.", () -> localQueue);
        } catch (Throwable e) {
            log.error("Error during dashboard RabbitMQ setup.", e);
        }
    }

    private void handleMessage(MessageEvent messageEvent) {
        if ("launchClient".equals(messageEvent.getMessage().getAttributes().get("header.acuity-type"))) {
            String connectionId = UUID.randomUUID().toString();

            if (messageEvent.getMessage().getBody() != null){
                try {
                    connectionsDb.update("connections", "RPC_" + connectionId, messageEvent.getMessage().getBody());
                    Document connections = connectionsDb.findByGroupAndKey("connections", "RPC_" + connectionId);
                    System.out.println();
                } catch (MessagingException e) {
                    log.error("Error saving client configuration.", e);
                    return;
                }
            }

            rsPeerService.launch(connectionId);
        }
    }

    @Override
    public void run(String... strings) throws Exception {
        connect();
    }
}
