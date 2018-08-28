package com.acuitybotting.bot_control.services.rabbit;

import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitHub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
@Service
@Slf4j
public class BotControlRabbitService implements CommandLineRunner {

    private final ApplicationEventPublisher publisher;

    private RabbitHub rabbitHub = new RabbitHub();

    @Value("${rabbit.username}")
    private String username;
    @Value("${rabbit.password}")
    private String password;

    @Autowired
    public BotControlRabbitService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    private void connect() {
        try {
            rabbitHub.auth(username, password);
            rabbitHub.start();

            rabbitHub.getLocalQueue()
                    .bind("amq.rabbitmq.event", "connection.#")
                    .withListener(publisher::publishEvent);

            rabbitHub.createPool(10, channel -> {
                channel.createQueue("acuitybotting.work.bot-control", false)
                        .withListener(publisher::publishEvent)
                        .open(false);
            });
        } catch (Throwable e) {
            log.error("Error during dashboard RabbitMQ setup.", e);
        }
    }

    @Override
    public void run(String... strings) {
        connect();
    }
}
