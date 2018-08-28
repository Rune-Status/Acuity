package com.acuitybotting.website.dashboard.services;

import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.db.arangodb.repositories.connections.RegisteredConnectionRepository;
import com.acuitybotting.website.dashboard.DashboardRabbitService;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 8/15/2018.
 */
@Service
@UIScope
public class ClientsService {

    private final RegisteredConnectionRepository connectionRepository;
    private final DashboardRabbitService rabbitService;

    public ClientsService(RegisteredConnectionRepository connectionRepository, DashboardRabbitService rabbitService) {
        this.connectionRepository = connectionRepository;
        this.rabbitService = rabbitService;
    }

    public void kill(String clientId){
        String queue = "user." + Authentication.getAcuityPrincipalId() + ".queue." + clientId;
        try {
            rabbitService.getRabbitChannel().buildMessage(
                    "",
                    queue,
                    "{}"
            ).setAttribute("killConnection", "").send();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
