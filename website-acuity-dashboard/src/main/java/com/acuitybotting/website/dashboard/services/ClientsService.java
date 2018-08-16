package com.acuitybotting.website.dashboard.services;

import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.website.dashboard.DashboardRabbitService;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 8/15/2018.
 */
@Service
@UIScope
public class ClientsService {

    private final RabbitDbService rabbitDbService;
    private final DashboardRabbitService rabbitService;

    public ClientsService(RabbitDbService rabbitDbService, DashboardRabbitService rabbitService) {
        this.rabbitDbService = rabbitDbService;
        this.rabbitService = rabbitService;
    }

    public Set<GsonRabbitDocument> loadClients() {
        return rabbitDbService
                .loadByGroup(RabbitDbService.buildQueryMap(Authentication.getAcuityPrincipalId(), "services.registered-connections", "connections"), GsonRabbitDocument.class)
                .stream()
                .filter(connection -> connection.getSubKey().startsWith("RPC_") && (boolean) connection.getHeaders().getOrDefault("connected", false))
                .collect(Collectors.toSet());
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
