package com.acuitybotting.client.bot.control;

import com.acuitybotting.client.bot.control.domain.AcuityConnectionConfig;
import com.acuitybotting.client.bot.control.interfaces.ControlInterface;
import com.acuitybotting.client.bot.control.interfaces.StateInterface;
import com.acuitybotting.common.utils.ConnectionKeyUtil;
import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitHub;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.channel.RabbitChannel;
import com.acuitybotting.data.flow.messaging.services.db.implementations.rabbit.RabbitDb;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zachary Herridge on 8/14/2018.
 */
public class AcuityHub {

    private static RabbitHub rabbitHub = new RabbitHub();

    private static ControlInterface controlInterface;
    private static StateInterface stateInterface;

    private static ScheduledExecutorService scheduledExecutorService = ExecutorUtil.newScheduledExecutorPool(2);

    public static void start() {
        Optional<AcuityConnectionConfig> configOptional = Optional.ofNullable(System.getProperty("acuityConfig"))
                .map(s -> s.isEmpty() ? null : s)
                .map(s -> new Gson().fromJson(new String(Base64.getDecoder().decode(s)), AcuityConnectionConfig.class));

        AcuityConnectionConfig connectionConfig = configOptional.orElse(new AcuityConnectionConfig());

        if (connectionConfig.getConnectionKey() == null) connectionConfig.setConnectionKey(ConnectionKeyUtil.findKey());
        if (connectionConfig.getConnectionId() == null) connectionConfig.setConnectionId(UUID.randomUUID().toString());

        String username = "acuity-guest";
        String password = "";

        if (connectionConfig.getConnectionKey() != null) {
            JsonObject jsonObject = ConnectionKeyUtil.decode(connectionConfig.getConnectionKey());
            username = jsonObject.get("principalId").getAsString();
            password = jsonObject.get("secret").getAsString();
        }

        rabbitHub.auth(username, password);
        rabbitHub.start("RPC", connectionConfig.getConnectionId());

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

    private static void startAuthedServices(){
        getScheduledExecutor().scheduleAtFixedRate(AcuityHub::sendPlayer, 100, 5, TimeUnit.SECONDS);
        getScheduledExecutor().scheduleAtFixedRate(AcuityHub::sendClient, 100, 5, TimeUnit.SECONDS);
    }

    private static void sendPlayer(){
        if (stateInterface == null) return;

        Map<String, Object> playerUpdate = stateInterface.buildPlayerState();
        if (playerUpdate == null || playerUpdate.get("email") == null) return;

        try {
            RabbitDb db = rabbitHub.getDb("services.rs-accounts");
            db.update("players", (String) playerUpdate.get("email"), new Gson().toJson(playerUpdate));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static void sendClient(){
        if (stateInterface == null) return;

        Map<String, Object> clientUpdate = stateInterface.buildClientState();
        if (clientUpdate == null) return;

        try {
            RabbitDb db = rabbitHub.getDb("services.registered-connections");
            db.update("connections", rabbitHub.getConnectionId(), new Gson().toJson(clientUpdate));
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
        start();
        while (true) {

        }
    }
}
