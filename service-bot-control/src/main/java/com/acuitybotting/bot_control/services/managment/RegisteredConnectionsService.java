package com.acuitybotting.bot_control.services.managment;

import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Service
@Slf4j
public class RegisteredConnectionsService {

    private final RabbitDbService rabbitDbService;

    @Value("${rabbit.username}")
    private String username;
    @Value("${rabbit.password}")
    private String password;

    @Autowired
    public RegisteredConnectionsService(RabbitDbService rabbitDbService) {
        this.rabbitDbService = rabbitDbService;
    }

    @Scheduled(fixedDelay = 4000)
    public void updateConnections() {
        String updateConnected = "FOR r IN @@collection\n" +
                "FILTER r.database == 'services.registered-connections'" +
                "LET connected = r.meta._lastUpdateTime > @timeout\n" +
                "UPDATE { _key: r._key, subDocument : { connected : connected}} IN @@collection";

        rabbitDbService.query(updateConnected)
                .withParam("timeout", System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(20))
                .execute();
    }
}
