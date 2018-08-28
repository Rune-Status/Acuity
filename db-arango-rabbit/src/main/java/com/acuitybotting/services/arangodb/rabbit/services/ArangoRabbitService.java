package com.acuitybotting.services.arangodb.rabbit.services;

import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitHub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ArangoRabbitService implements CommandLineRunner {

    private final ApplicationEventPublisher publisher;

    private RabbitHub rabbitHub = new RabbitHub();

    @Value("${rabbit.username}")
    private String username;
    @Value("${rabbit.password}")
    private String password;

    @Autowired
    public ArangoRabbitService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    private void connect() {
        try {
            rabbitHub.auth(username, password);
            rabbitHub.start();

            rabbitHub.createPool(10, channel -> {
                try {
                    channel.createQueue("acuitybotting.work.arangodb", true)
                            .bind("acuitybotting.general", "user.*.services.arangodb.request")
                            .withListener(publisher::publishEvent)
                            .withListener(System.out::println)
                            .open(false);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
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
