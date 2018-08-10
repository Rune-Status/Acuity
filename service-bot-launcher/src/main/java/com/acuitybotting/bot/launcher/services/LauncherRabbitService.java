package com.acuitybotting.bot.launcher.services;

import com.acuitybotting.bot.launcher.utils.CommandLine;
import com.acuitybotting.common.utils.JwtUtil;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

/**
 * Created by Zachary Herridge on 8/6/2018.
 */
@Service
@Slf4j
public class LauncherRabbitService implements CommandLineRunner {

    private void connect() {
        try {
            String jwt = RSPeerEnviroment.getSession();
            Objects.requireNonNull(jwt, "Failed to acquire user jwt.");
            String sub = JwtUtil.decodeBody(jwt).get("sub").getAsString();
            String allowedPrefix = "user." + sub + ".";

            RabbitClient rabbitClient = new RabbitClient();
            rabbitClient.auth("195.201.248.164", sub, jwt);
            rabbitClient.connect("ABL_" + UUID.randomUUID().toString());
            MessagingChannel channel = rabbitClient.openChannel();

            channel.createQueue(allowedPrefix + "queue." + rabbitClient.getRabbitId(), true)
                    .withListener(this::handleMessage)
                    .open(true);
        } catch (Throwable e) {
            log.error("Error during dashboard RabbitMQ setup.", e);
        }
    }

    private void handleMessage(MessageEvent messageEvent) {
        if ("runCommand".equals(messageEvent.getMessage().getAttributes().get("type"))) {
            JsonElement launchConfig = new Gson().fromJson(messageEvent.getMessage().getBody(), JsonElement.class);
            log.info("Got launch config: ", launchConfig);
            String command = CommandLine.replacePlaceHolders(launchConfig.getAsJsonObject().get("command").getAsString());

            log.info("Running command: {}", command);

            try {
                CommandLine.runCommand(command);
            } catch (Throwable e) {
                log.error("Error running command.", e);
            }
        }
    }

    @Override
    public void run(String... strings) throws Exception {
        connect();
    }
}
