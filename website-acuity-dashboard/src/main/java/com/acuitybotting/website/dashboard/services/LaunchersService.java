package com.acuitybotting.website.dashboard.services;

import com.acuitybotting.db.arangodb.repositories.acuity.principal.service.AcuityUsersService;
import com.acuitybotting.db.arangodb.repositories.connections.RegisteredConnectionRepository;
import com.acuitybotting.db.arangodb.repositories.resources.accounts.domain.RsAccount;
import com.acuitybotting.db.arangodb.repositories.resources.proxies.domain.Proxy;
import com.acuitybotting.website.dashboard.DashboardRabbitService;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by Zachary Herridge on 8/15/2018.
 */
@Service
@UIScope
public class LaunchersService {

    private final RegisteredConnectionRepository connectionRepository;
    private final AcuityUsersService acuityUsersService;
    private final DashboardRabbitService rabbitService;

    public LaunchersService(RegisteredConnectionRepository connectionRepository, AcuityUsersService acuityUsersService, DashboardRabbitService rabbitService) {
        this.connectionRepository = connectionRepository;
        this.acuityUsersService = acuityUsersService;
        this.rabbitService = rabbitService;
    }

    public void deploy(Set<String> subIds, String command, RsAccount rsAccount, Proxy proxy, boolean localScript, String scriptArgs, String scriptSelector, String world) {
        /*Notifications.display("Deploying to {} launchers.", subIds.size());

        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
        connectionConfiguration.setConnectionId(UUID.randomUUID().toString());

        JsonObject clientConfiguration = new JsonObject();

        AcuityBottingUser acuityBottingUser = acuityUsersService.findUserByUid(Authentication.getAcuityPrincipalId()).orElse(null);
        if (acuityBottingUser == null) return;
        clientConfiguration.addProperty("masterSecret", acuityBottingUser.getMasterKey());

        if (rsAccount != null) {
            clientConfiguration.addProperty("accountLogin", rsAccount.getParent().getSubKey());
            clientConfiguration.addProperty("accountEncryptedPassword", rsAccount.getEncryptedPassword());
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

        Notifications.display("Deployment complete.");*/
    }
}
