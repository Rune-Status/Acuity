package com.acuitybotting.bot_control;

import com.acuitybotting.bot_control.services.user.db.RabbitDbService;
import com.acuitybotting.db.arango.acuity.identities.service.PrincipalLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by Zachary Herridge on 7/25/2018.
 */
@Component
@Slf4j
public class BotControlRunner implements CommandLineRunner {

    private final PrincipalLinkService service;

    @Autowired
    public BotControlRunner(PrincipalLinkService service) {
        this.service = service;
    }


    @Override
    public void run(String... strings) throws Exception {
    }
}
