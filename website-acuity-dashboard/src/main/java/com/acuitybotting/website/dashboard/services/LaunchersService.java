package com.acuitybotting.website.dashboard.services;

import com.acuitybotting.common.utils.connection_configuration.ConnectionConfigurationUtil;
import com.acuitybotting.common.utils.connection_configuration.domain.ConnectionConfiguration;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.LauncherConnection;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.Proxy;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.RsAccountInfo;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.website.dashboard.DashboardRabbitService;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.acuitybotting.website.dashboard.utils.Notifications;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 8/15/2018.
 */
@Service
@SessionScope
public class LaunchersService {

    private final RabbitDbService rabbitDbService;
    private final DashboardRabbitService rabbitService;

    public LaunchersService(RabbitDbService rabbitDbService, DashboardRabbitService rabbitService) {
        this.rabbitDbService = rabbitDbService;
        this.rabbitService = rabbitService;
    }

    public Set<LauncherConnection> loadLaunchers() {
        return rabbitDbService
                .loadByGroup(RabbitDbService.buildQueryMap(Authentication.getAcuityPrincipalId(), "services.registered-connections", "connections"), GsonRabbitDocument.class)
                .stream()
                .filter(connection -> connection.getSubKey().startsWith("ABL_") && (boolean) connection.getHeaders().getOrDefault("connected", false))
                .map(gsonRabbitDocument -> gsonRabbitDocument.getSubDocumentAs(LauncherConnection.class))
                .collect(Collectors.toSet());
    }

    public void deploy(Set<String> subIds, String command, RsAccountInfo rsAccountInfo, Proxy proxy) {
        Notifications.display("Deploying to {} launchers.", subIds.size());

        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
        connectionConfiguration.setConnectionId(UUID.randomUUID().toString());

        Map<String, Object> launchConfig = new HashMap<>();
        launchConfig.put("command", command);
        launchConfig.put("acuityConnectionConfiguration", ConnectionConfigurationUtil.encode(connectionConfiguration));

        String launchJson = new Gson().toJson(launchConfig);
        for (String subId : subIds) {
            String queue = "user." + Authentication.getAcuityPrincipalId() + ".queue." + subId;
            try {
                rabbitService.getRabbitChannel().buildMessage(
                        "",
                        queue,
                        launchJson
                ).setAttribute("type", "runCommand").send();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        Notifications.display("Deployment complete.");
    }
}
