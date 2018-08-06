package com.acuitybotting.discord.bot.services.rabbit;

import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.management.RabbitManagement;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by Zachary Herridge on 8/6/2018.
 */
@Service
@Getter
@Slf4j
public class DiscordBotRabbitService implements CommandLineRunner {

    @Value("${rabbit.host}")
    private String host;
    @Value("${rabbit.username}")
    private String username;
    @Value("${rabbit.password}")
    private String password;

    private final ApplicationEventPublisher publisher;

    @Autowired
    public DiscordBotRabbitService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void loadAll() {
        try {
            RabbitManagement.loadAll("http://" + host + ":" + "15672", username, password);
        } catch (Exception e) {
            log.error("Error during loading Rabbit management connections.", e);
        }
    }

    private void connect() {
        try {
            RabbitClient rabbitClient = new RabbitClient();
            rabbitClient.auth(host, username, password);
            rabbitClient.connect("ADB_" + UUID.randomUUID().toString());
            MessagingChannel channel = rabbitClient.openChannel();

            channel.createQueue("acuitybotting.work.discord-bot", false)
                    .withListener(publisher::publishEvent)
                    .open(false);
        } catch (Throwable e) {
            log.error("Error during dashboard RabbitMQ setup.", e);
        }
    }

    @Override
    public void run(String... strings) throws Exception {
        connect();
    }
}
