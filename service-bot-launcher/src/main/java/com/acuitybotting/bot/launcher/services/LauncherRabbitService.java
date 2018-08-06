package com.acuitybotting.bot.launcher.services;

import com.acuitybotting.common.utils.JwtUtil;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final RSPeerService rsPeerService;

    private MessagingChannel channel;

    private String sub;
    private String allowedPrefix;
    private RabbitQueue localQueue;

    @Autowired
    public LauncherRabbitService(RSPeerService rsPeerService) {
        this.rsPeerService = rsPeerService;
    }

    private void connect() {
        try {
            String jwt = rsPeerService.getSession();
            Objects.requireNonNull(jwt, "Failed to acquire user jwt.");
            sub = JwtUtil.decodeBody(jwt).get("sub").getAsString();
            allowedPrefix = "user." + sub + ".";

            RabbitClient rabbitClient = new RabbitClient();
            rabbitClient.auth("195.201.248.164", sub, jwt);
            rabbitClient.connect("ABL_001_" + UUID.randomUUID().toString());
            channel = rabbitClient.openChannel();
            localQueue = channel.createQueue(allowedPrefix + "queue." + rabbitClient.getRabbitId(), true).open(true);
        } catch (Throwable e) {
            log.error("Error during dashboard RabbitMQ setup.", e);
        }
    }

    @Override
    public void run(String... strings) throws Exception {
        connect();
    }
}
