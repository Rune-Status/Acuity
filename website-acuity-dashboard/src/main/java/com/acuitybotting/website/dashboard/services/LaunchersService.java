package com.acuitybotting.website.dashboard.services;

import com.acuitybotting.common.utils.configurations.ConnectionConfiguration;
import com.acuitybotting.common.utils.configurations.utils.ConnectionConfigurationUtil;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.db.implementations.rabbit.RabbitDb;
import com.acuitybotting.db.arango.acuity.identities.domain.AcuityBottingUser;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityUsersService;
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
    private final AcuityUsersService acuityUsersService;
    private final DashboardRabbitService rabbitService;

    public LaunchersService(RabbitDbService rabbitDbService, AcuityUsersService acuityUsersService, DashboardRabbitService rabbitService) {
        this.rabbitDbService = rabbitDbService;
        this.acuityUsersService = acuityUsersService;
        this.rabbitService = rabbitService;
    }

    public Set<LauncherConnection> loadLaunchers() {
        return rabbitDbService.queryByGroup()
                .withMatch(Authentication.getAcuityPrincipalId(), "services.registered-connections", "connections")
                .findAll(GsonRabbitDocument.class)
                .stream()
                .filter(connection -> connection.getSubKey().startsWith("ABL_") && (boolean) connection.getMeta().getOrDefault("connected", false))
                .map(gsonRabbitDocument -> gsonRabbitDocument.getSubDocumentAs(LauncherConnection.class))
                .collect(Collectors.toSet());
    }

    public void deploy(Set<String> subIds, String command, RsAccountInfo rsAccountInfo, Proxy proxy, boolean localScript, String scriptArgs, String scriptSelector, String world) {
        Notifications.display("Deploying to {} launchers.", subIds.size());

        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
        connectionConfiguration.setConnectionId(UUID.randomUUID().toString());

        JsonObject clientConfiguration = new JsonObject();

        AcuityBottingUser acuityBottingUser = acuityUsersService.findUserByUid(Authentication.getAcuityPrincipalId()).orElse(null);
        if (acuityBottingUser == null) return;
        clientConfiguration.addProperty("masterSecret", acuityBottingUser.getMasterKey());

        if (rsAccountInfo != null) {
            clientConfiguration.addProperty("accountLogin", rsAccountInfo.getParent().getSubKey());
            clientConfiguration.addProperty("accountEncryptedPassword", rsAccountInfo.getEncryptedPassword());
        }

        if (proxy != null) {
            clientConfiguration.addProperty("proxyHost", proxy.getHost());
            clientConfiguration.addProperty("proxyPort", proxy.getPort());
            clientConfiguration.addProperty("proxyUsername", proxy.getUsername());
            clientConfiguration.addProperty("proxyEncryptedPassword", proxy.getEncryptedPassword());
        }

        if (scriptSelector != null){
            clientConfiguration.addProperty("scriptLocal", localScript);
            clientConfiguration.addProperty("scriptSelector", scriptSelector);
            clientConfiguration.addProperty("scriptArgs", scriptArgs);
        }

        if (world != null){
            clientConfiguration.addProperty("world", Integer.parseInt(world));
        }

        Map<String, Object> headers = new HashMap<>();
        headers.put("connectionConfirmationTime", System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30));

        String configurationDoc = new Gson().toJson(Collections.singletonMap("configuration", clientConfiguration));

        rabbitDbService.query()
                .withMatch(Authentication.getAcuityPrincipalId(), "services.registered-connections", "connections", "RPC_" + connectionConfiguration.getConnectionId())
                .upsert(headers, configurationDoc, configurationDoc);

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
