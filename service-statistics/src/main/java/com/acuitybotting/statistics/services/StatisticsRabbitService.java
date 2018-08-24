package com.acuitybotting.statistics.services;

import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 8/21/2018.
 */
@Service
public class StatisticsRabbitService implements CommandLineRunner {

    @Value("${rabbit.username}")
    private String username;

    @Value("${rabbit.password}")
    private String password;

    private final ApplicationEventPublisher publisher;
    private final RsBuddyService rsBuddyService;

    public StatisticsRabbitService(ApplicationEventPublisher publisher, RsBuddyService rsBuddyService) {
        this.publisher = publisher;
        this.rsBuddyService = rsBuddyService;
    }

    private void start(){
        RabbitHub rabbitHub = new RabbitHub();
        rabbitHub.auth(username, password);
        rabbitHub.start();

        try {
            rabbitHub.getLocalQueue().bind("acuitybotting.general", "user.*.rabbitdb.update.#");
            rabbitHub.getLocalQueue().bind("acuitybotting.general", "user.*.hub-event.#");
            rabbitHub.getLocalQueue().withListener(publisher::publishEvent);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... args) throws Exception {
        start();
    }
}
