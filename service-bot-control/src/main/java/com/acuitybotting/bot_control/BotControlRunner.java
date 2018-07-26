package com.acuitybotting.bot_control;

import com.acuitybotting.bot_control.services.user.db.RabbitDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by Zachary Herridge on 7/25/2018.
 */
@Component
public class BotControlRunner implements CommandLineRunner {

    private final RabbitDbService rabbitDbService;

    @Autowired
    public BotControlRunner(RabbitDbService rabbitDbService) {
        this.rabbitDbService = rabbitDbService;
    }

    @Override
    public void run(String... strings) throws Exception {
        
    }
}
