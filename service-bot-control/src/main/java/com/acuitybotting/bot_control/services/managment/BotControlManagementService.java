package com.acuitybotting.bot_control.services.managment;

import com.acuitybotting.bot_control.services.user.db.RabbitDbService;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.management.RabbitManagement;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.management.domain.RabbitConnection;
import com.acuitybotting.data.flow.messaging.services.db.domain.RabbitDbRequest;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Service
@Slf4j
public class BotControlManagementService {

    private final RabbitDbService rabbitDbService;

    @Value("${rabbit.host}")
    private String host;
    @Value("${rabbit.username}")
    private String username;
    @Value("${rabbit.password}")
    private String password;

    @Autowired
    public BotControlManagementService(RabbitDbService rabbitDbService) {
        this.rabbitDbService = rabbitDbService;
    }

    private void updateRegisteredConnections(){
        for (Map.Entry<String, List<RabbitConnection>> entry : RabbitManagement.getConnections().entrySet()) {
            RabbitDbRequest rabbitRabbitDbRequest = new RabbitDbRequest();
            rabbitRabbitDbRequest.setType(RabbitDbRequest.SAVE_UPDATE);
            rabbitRabbitDbRequest.setDatabase("registered-connections");
            rabbitRabbitDbRequest.setGroup("connections");

            for (RabbitConnection rabbitConnection : entry.getValue()) {
                if (rabbitConnection.getUser_provided_name() == null) continue;

                Map<String, Object> headers = new HashMap<>();
                headers.put("connected", true);
                headers.put("connectionTime", rabbitConnection.getConnected_at());
                headers.put("connectionConfirmationTime", System.currentTimeMillis());
                headers.put("peerHost", rabbitConnection.getPeer_host());
                rabbitRabbitDbRequest.setKey(rabbitConnection.getUser_provided_name());
                rabbitDbService.save(entry.getKey(), rabbitRabbitDbRequest, headers);
            }
        }

        String updateTimeout = "FOR r IN RabbitDocument\n" +
                "FILTER r.headers.connectionConfirmationTime != NULL\n" +
                "FILTER r.headers.connectionConfirmationTime < @timeout\n" +
                "UPDATE { _key: r._key, headers: { connected : false}} IN RabbitDocument";
        rabbitDbService.getArangoOperations().query(updateTimeout, Collections.singletonMap("timeout", System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(11)), null, null);
    }

    @Scheduled(fixedDelay = 10000)
    public void updateConnections(){
        try {
            RabbitManagement.loadAll("http://" + host + ":" + "15672", username, password);
            updateRegisteredConnections();
        } catch (Exception e) {
            log.error("Error during RabbitManagement.loadAll.", e);
        }
    }

    @EventListener
    public void handleRequest(MessageEvent messageEvent) {
        if (messageEvent.getRouting().equals("connection.created")){
            updateConnections();
        }

        if (messageEvent.getRouting().equals("connection.closed")){
            String userProvidedName = messageEvent.getMessage().getAttributes().get("header.user_provided_name");
            if (userProvidedName == null) return;

            String singleUpdate =
                    "FOR r IN RabbitDocument\n" +
                    "FILTER r.database == 'registered-connections'\n" +
                    "FILTER r.subGroup == 'connections'\n" +
                    "FILTER r.subKey == @userDefinedName\n" +
                    "UPDATE { _key: r._key, headers: { connected : false}} IN RabbitDocument";
            rabbitDbService.getArangoOperations().query(singleUpdate, Collections.singletonMap("userDefinedName", userProvidedName), null, null);
        }
    }
}
