package com.acuitybotting.bot_control;

import com.acuitybotting.bot_control.domain.RabbitDbRequest;
import com.acuitybotting.bot_control.services.user.db.RabbitDbService;
import com.acuitybotting.db.arango.acuity.bot_control.domain.RabbitDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

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
