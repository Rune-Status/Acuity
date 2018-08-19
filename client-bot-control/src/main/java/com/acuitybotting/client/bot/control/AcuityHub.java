package com.acuitybotting.client.bot.control;

import com.acuitybotting.client.bot.control.domain.ClientConfiguration;
import com.acuitybotting.client.bot.control.interfaces.ControlInterface;
import com.acuitybotting.client.bot.control.interfaces.StateInterface;
import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.common.utils.configurations.ConnectionConfiguration;
import com.acuitybotting.common.utils.configurations.utils.ConnectionConfigurationUtil;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitHub;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.channel.RabbitChannel;
import com.acuitybotting.data.flow.messaging.services.db.domain.Document;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rockaport.alice.Alice;
import com.rockaport.alice.AliceContext;
import com.rockaport.alice.AliceContextBuilder;

import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zachary Herridge on 8/14/2018.
 */
public class AcuityHub {

    private static ConnectionConfiguration connectionConfiguration;

    private static RabbitHub rabbitHub = new RabbitHub();

    private static ControlInterface controlInterface;
    private static StateInterface stateInterface;

    private static ExecutorService executorService = ExecutorUtil.newExecutorPool(1);
    private static ScheduledExecutorService scheduledExecutorService = ExecutorUtil.newScheduledExecutorPool(1);

    private static boolean guestAccount = true;

    public static void start(String type, String version) {
        connectionConfiguration = ConnectionConfigurationUtil.decode(ConnectionConfigurationUtil.find()).orElse(new ConnectionConfiguration());

        if (connectionConfiguration.getConnectionId() == null)
            connectionConfiguration.setConnectionId(UUID.randomUUID().toString());

        String username = "acuity-guest";
        String password = "";

        if (connectionConfiguration.getConnectionKey() != null) {
            JsonObject jsonObject = ConnectionConfigurationUtil.decodeConnectionKey(connectionConfiguration.getConnectionKey());
            username = jsonObject.get("principalId").getAsString();
            password = jsonObject.get("secret").getAsString();
            guestAccount =  false;
        }

        rabbitHub.auth(username, password);
        rabbitHub.start(type, version, connectionConfiguration.getConnectionId());

        long start = System.currentTimeMillis();
        while ((System.currentTimeMillis() - start) < TimeUnit.SECONDS.toMillis(15) && !rabbitHub.getLocalQueue().getChannel().isConnected()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!guestAccount) {
            pullAndApplyConfiguration();
            startAuthedServices();

            rabbitHub.getLocalQueue().withListener(messageEvent -> {
                if (messageEvent.getMessage().getAttributes().containsKey("killConnection")) {
                    System.exit(0);
                }
            });
        }
    }

    public static boolean publishEvent(String type, String body){
        RabbitChannel channel = rabbitHub.getLocalPool().getChannel();
        if (channel == null) return false;
        executorService.submit(() -> {
            try {
                channel.createMessage()
                        .setTargetExchange(rabbitHub.getGeneralExchange())
                        .setTargetRouting(rabbitHub.getAllowedPrefix() + "hub-event." + type)
                        .setAttribute("eventType",  type)
                        .setBody(body)
                        .send();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
        return true;
    }

    private static void pullAndApplyConfiguration() {
        try {
            Document connection = rabbitHub.getDb("services.registered-connections").findByGroupAndKey("connections", rabbitHub.getConnectionId());
            if (connection == null || connection.getDocument() == null || connection.getDocument().get("configuration") == null)
                return;

            ClientConfiguration configuration = new Gson().fromJson(connection.getDocument().getAsJsonObject("configuration"), ClientConfiguration.class);

            if (configuration.getAccountLogin() != null && configuration.getAccountEncryptedPassword() != null) {
                getControlInterface().ifPresent(control -> {
                    try {
                        String password = decrypt(decrypt(connectionConfiguration.getMasterKey(), configuration.getMasterSecret()), configuration.getAccountEncryptedPassword());
                        control.applyAccount(
                                configuration.getAccountLogin(),
                                password
                        );
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                });
            }

            if (configuration.getWorld() != null){
                getControlInterface().ifPresent(control -> {
                    control.applyWorld(configuration.getWorld());
                });
            }

            if (configuration.getScriptSelector() != null) {
                getControlInterface().ifPresent(control -> control.applyScript(
                        configuration.getScriptSelector(),
                        configuration.isScriptLocal(),
                        configuration.getScriptArgs()
                ));
            }

            if (configuration.getProxyHost() != null && configuration.getProxyPort() != null) {
                getControlInterface().ifPresent(control -> {
                    try {
                        String password = decrypt(decrypt(connectionConfiguration.getMasterKey(), configuration.getMasterSecret()), configuration.getProxyEncryptedPassword());
                        control.applyProxy(
                                configuration.getProxyHost(),
                                configuration.getProxyPort(),
                                configuration.getProxyUsername(),
                                password
                        );
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static String decrypt(String key, String value) throws GeneralSecurityException {
        return new String(getAlice().decrypt(Base64.getDecoder().decode(value), key.toCharArray()));
    }

    private static Alice getAlice() {
        return new Alice(new AliceContextBuilder().setKeyLength(AliceContext.KeyLength.BITS_128).build());
    }

    private static void startAuthedServices() {
        getScheduledExecutor().scheduleAtFixedRate(AcuityHub::sendPlayer, 1, 5, TimeUnit.SECONDS);
        getScheduledExecutor().scheduleAtFixedRate(AcuityHub::sendClient, 1, 5, TimeUnit.SECONDS);
    }

    private static void sendPlayer() {
        if (stateInterface == null) return;

        JsonObject playerUpdate = stateInterface.buildPlayerState();
        if (playerUpdate == null || playerUpdate.get("email") == null) return;

        JsonObject wrapper = new JsonObject();
        wrapper.add("gameInfo", playerUpdate);

        try {
            rabbitHub.getDb("services.rs-accounts").update(
                    "players",
                    playerUpdate.get("email").getAsString(),
                    new Gson().toJson(wrapper)
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static void sendClient() {
        if (stateInterface == null) return;

        JsonObject clientUpdate = stateInterface.buildClientState();
        if (clientUpdate == null) return;


        JsonObject wrapper = new JsonObject();
        wrapper.add("state", clientUpdate);

        try {
            rabbitHub.getDb("services.registered-connections").update(
                    "connections",
                    rabbitHub.getConnectionId(),
                    new Gson().toJson(wrapper)
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static ScheduledExecutorService getScheduledExecutor() {
        return scheduledExecutorService;
    }

    public static Optional<ControlInterface> getControlInterface() {
        return Optional.ofNullable(controlInterface);
    }

    public static void setControlInterface(ControlInterface controlInterface) {
        AcuityHub.controlInterface = controlInterface;
    }

    public static Optional<StateInterface> getStateInterface() {
        return Optional.ofNullable(stateInterface);
    }

    public static void setStateInterface(StateInterface stateInterface) {
        AcuityHub.stateInterface = stateInterface;
    }

    public static RabbitHub getRabbitHub() {
        return rabbitHub;
    }

    public static void main(String[] args) {
        start("RPC", "test");
    }
}
