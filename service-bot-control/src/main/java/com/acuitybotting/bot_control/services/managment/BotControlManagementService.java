package com.acuitybotting.bot_control.services.managment;

import com.acuitybotting.bot_control.domain.RabbitDbRequest;
import com.acuitybotting.bot_control.services.user.db.RabbitDbService;
import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.management.RabbitManagement;
import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.management.domain.RabbitConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            RabbitDbRequest rabbitDbRequest = new RabbitDbRequest();
            rabbitDbRequest.setType(RabbitDbRequest.SAVE_UPDATE);
            rabbitDbRequest.setDatabase("registered-connections");
            rabbitDbRequest.setGroup("connections");

            for (RabbitConnection rabbitConnection : entry.getValue()) {
                if (rabbitConnection.getUser_provided_name() == null) continue;

                Map<String, Object> headers = new HashMap<>();
                headers.put("connectionTime", rabbitConnection.getConnected_at());
                headers.put("connectionConfirmationTime", System.currentTimeMillis());
                rabbitDbRequest.setKey(rabbitConnection.getUser_provided_name());
                rabbitDbService.save(entry.getKey(), rabbitDbRequest, headers);
            }
        }
    }

    //@Scheduled(fixedDelay = 20000)
    public void updateConnections(){
        try {
            RabbitManagement.loadAll("http://" + host + ":" + "15672", username, password);
            updateRegisteredConnections();
        } catch (Exception e) {
            log.error("Error during RabbitManagement.loadAll.", e);
        }
    }
}
