package com.acuitybotting.bot_control;

import com.acuitybotting.db.arango.acuity.rabbit_db.repository.RabbitDocumentRepository;
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
    private final RabbitDocumentRepository rabbitDocumentRepository;

    @Autowired
    public BotControlRunner(PrincipalLinkService service, RabbitDocumentRepository rabbitDocumentRepository) {
        this.service = service;
        this.rabbitDocumentRepository = rabbitDocumentRepository;
    }


    @Override
    public void run(String... strings) throws Exception {

    }
}
