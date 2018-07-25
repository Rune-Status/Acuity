package com.acuitybotting.bot_control;

import com.acuitybotting.bot_control.domain.RabbitDbRequest;
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

        RabbitDbRequest rabbitDbRequest = new RabbitDbRequest();
        rabbitDbRequest.setType(RabbitDbRequest.SAVE_REPLACE);
        rabbitDbRequest.setDatabase("testdb");
        rabbitDbRequest.setGroup("testgroup");
        rabbitDbRequest.setKey("testkey");
        rabbitDbRequest.setInsertDocument("[{\n" +
                "  \"principalKey\": \"f0d6726f-76a9-4d9e-b2c4-c534a58ee95c\",\n" +
                "  \"lastHeartbeatTime\": 1532244214037,\n" +
                "  \"connectionId\": \"68f36018-d2e1-4e50-a4b9-ad761a0452e2\",\n" +
                "  \"attributes\": {},\n" +
                "  \"_class\": \"com.acuitybotting.db.arango.acuity.bot_control.domain.RegisteredConnection\",\n" +
                "  \"connectionType\": \"rspeer-client\",\n" +
                "  \"connectionTime\": 1532242344041\n" +
                "}]");
       // rabbitDbService.save("asdsad", rabbitDbRequest, null);

        rabbitDbService.loadByKey("asdsad", rabbitDbRequest);
    }
}
