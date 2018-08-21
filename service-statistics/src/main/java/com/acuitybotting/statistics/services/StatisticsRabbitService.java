package com.acuitybotting.statistics.services;

import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitHub;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 8/21/2018.
 */
@Service
public class StatisticsRabbitService implements CommandLineRunner {

    private final RsBuddyService rsBuddyService;

    public StatisticsRabbitService(RsBuddyService rsBuddyService) {
        this.rsBuddyService = rsBuddyService;
    }

    private void start(){
        RabbitHub rabbitHub = new RabbitHub();
        rabbitHub.start("ADS", "1.0.01");

        try {
            rabbitHub.getLocalQueue().bind("acuitybotting.general", "users.*.rabbitdb.update.#");
            rabbitHub.getLocalQueue().withListener(messageEvent -> System.out.println(messageEvent.getMessage().getBody()));
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run(String... args) throws Exception {
        rsBuddyService.getItemPrices();
        System.out.println();
    }
}
