package com.acuitybotting.statistics.services;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.UpsertResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Zachary Herridge on 8/21/2018.
 */
@Service
public class ConnectionsStatisticsService {

    private final RabbitDbService rabbitDbService;

    public ConnectionsStatisticsService(RabbitDbService rabbitDbService) {
        this.rabbitDbService = rabbitDbService;
    }

    @Scheduled(initialDelay = 3000, fixedRate = 3000)
    private void update(){
        String updateTimeout =
                "FOR r IN @@collection\n" +
                "FILTER r.headers.connected == true\n" +
                "RETURN r";

        Set<GsonRabbitDocument> all = rabbitDbService.query(updateTimeout).findAll(GsonRabbitDocument.class);



    }
}
