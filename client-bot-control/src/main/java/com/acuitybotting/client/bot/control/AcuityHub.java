package com.acuitybotting.client.bot.control;

import com.acuitybotting.client.bot.control.interfaces.ControlInterface;
import com.acuitybotting.client.bot.control.interfaces.StateInterface;
import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.common.utils.connection_configuration.ConnectionConfigurationUtil;
import com.acuitybotting.common.utils.connection_configuration.domain.ConnectionConfiguration;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitHub;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rockaport.alice.Alice;
import com.rockaport.alice.AliceContext;
import com.rockaport.alice.AliceContextBuilder;

import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
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

    private static ScheduledExecutorService scheduledExecutorService = ExecutorUtil.newScheduledExecutorPool(2);

    public static void start(String prefix) {
        connectionConfiguration = ConnectionConfigurationUtil.decode(ConnectionConfigurationUtil.find()).orElse(new ConnectionConfiguration());

        if (connectionConfiguration.getConnectionId() == null) connectionConfiguration.setConnectionId(UUID.randomUUID().toString());

        String username = "acuity-guest";
        String password = "";

        if (connectionConfiguration.getConnectionKey() != null) {
            JsonObject jsonObject = ConnectionConfigurationUtil.decodeConnectionKey(connectionConfiguration.getConnectionKey());
            username = jsonObject.get("principalId").getAsString();
            password = jsonObject.get("secret").getAsString();
        }

        rabbitHub.auth(username, password);
        rabbitHub.start(prefix, connectionConfiguration.getConnectionId());

        rabbitHub.getLocalQueue().withListener(messageEvent -> {
            if (messageEvent.getMessage().getAttributes().containsKey("killConnection")) {
                System.exit(0);
            }
        });

        long start = System.currentTimeMillis();
        while ((System.currentTimeMillis() - start) < TimeUnit.SECONDS.toMillis(15) && !rabbitHub.getLocalQueue().getChannel().isConnected()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!"acuity-guest".equals(username)) startAuthedServices();
    }

    public static String decrypt(String value) throws GeneralSecurityException {
        if (connectionConfiguration.getMasterKey() == null) return null;
        return new String(getAlice().decrypt(Base64.getDecoder().decode(value), connectionConfiguration.getMasterKey().toCharArray()));
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

        try {
            rabbitHub.getDb("services.rs-accounts").update(
                    "players",
                    playerUpdate.get("email").getAsString(),
                    new Gson().toJson(playerUpdate)
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static void sendClient() {
        if (stateInterface == null) return;

        JsonObject clientUpdate = stateInterface.buildClientState();
        if (clientUpdate == null) return;

        try {
            rabbitHub.getDb("services.registered-connections").update(
                    "connections",
                    rabbitHub.getConnectionId(),
                    new Gson().toJson(clientUpdate)
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
        start("RPC");
        while (true) {

        }
    }
}
