package com.acuitybotting.website.dashboard.services;

import com.acuitybotting.common.utils.configurations.ConnectionConfiguration;
import com.acuitybotting.common.utils.configurations.utils.ConnectionConfigurationUtil;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.db.implementations.rabbit.RabbitDb;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.LauncherConnection;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.Proxy;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.RsAccountInfo;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.website.dashboard.DashboardRabbitService;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.acuitybotting.website.dashboard.utils.Notifications;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 8/15/2018.
 */
@Service
@UIScope
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

        JsonObject clientConfiguration = new JsonObject();

        if (rsAccountInfo != null) {
            clientConfiguration.addProperty("rsEmail", rsAccountInfo.getSubKey());
            clientConfiguration.addProperty("rsEncryptedPassword", rsAccountInfo.getEncryptedPassword());
        }

        if (proxy != null) {
            clientConfiguration.addProperty("proxyHost", proxy.getHost());
            clientConfiguration.addProperty("proxyPort", proxy.getPort());
            clientConfiguration.addProperty("proxyUsername", proxy.getUsername());
            clientConfiguration.addProperty("proxyEncryptedPassword", proxy.getEncryptedPassword());
        }

        Map<String, Object> headers = new HashMap<>();
        headers.put("connectionConfirmationTime", System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30));


        String configurationDoc = new Gson().toJson(Collections.singletonMap("configuration", clientConfiguration));
        rabbitDbService.save(
                RabbitDb.STRATEGY_UPDATE,
                RabbitDbService.buildQueryMap(Authentication.getAcuityPrincipalId(), "services.registered-connections", "connections", "RPC_" + connectionConfiguration.getConnectionId()),
                headers,
                configurationDoc,
                configurationDoc
        );

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
