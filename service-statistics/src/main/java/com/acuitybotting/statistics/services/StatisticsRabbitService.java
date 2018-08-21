package com.acuitybotting.statistics.services;

import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitHub;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 8/21/2018.
 */
@Service
public class StatisticsRabbitService implements CommandLineRunner {

    private void start(){
        RabbitHub rabbitHub = new RabbitHub();
        rabbitHub.start("ADS", "1.0.01");
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
