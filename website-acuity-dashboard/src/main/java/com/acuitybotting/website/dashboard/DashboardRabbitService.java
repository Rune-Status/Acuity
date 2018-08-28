package com.acuitybotting.website.dashboard;

import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.channel.RabbitChannel;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.channel.RabbitChannelPool;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitHub;
import com.acuitybotting.db.arangodb.repositories.resources.accounts.RsAccountInfo;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Created by Zachary Herridge on 7/18/2018.
 */
@Service
@Slf4j
public class DashboardRabbitService implements CommandLineRunner {

    @Value("${rabbit.username}")
    private String username;

    @Value("${rabbit.password}")
    private String password;

    private final ApplicationEventPublisher publisher;
    private final ConfigurableApplicationContext applicationContext;

    private final RabbitDbService rabbitDbService;

    private RabbitChannelPool pool;

    @Autowired
    public DashboardRabbitService(ApplicationEventPublisher publisher, ConfigurableApplicationContext applicationContext, RabbitDbService rabbitDbService) {
        this.publisher = publisher;
        this.applicationContext = applicationContext;
        this.rabbitDbService = rabbitDbService;
    }

    private void connect(){
        try {

            RabbitHub rabbitHub = new RabbitHub();
            rabbitHub.auth(username, password);
            rabbitHub.start();
            pool = rabbitHub.createPool(5, null);
        }
        catch (Throwable e){
            log.error("Error during dashboard RabbitMQ setup.", e);
        }
    }

    public RabbitChannel getRabbitChannel() {
        return pool.getChannel();
    }

    @Override
    public void run(String... strings) throws Exception {
        RsAccountInfo rsAccountInfo = new RsAccountInfo();

        rsAccountInfo.setInventory(new HashMap<>());
        rsAccountInfo.setBank(new HashMap<>());

        rsAccountInfo.getInventory().put(543, 9888);

        rabbitDbService.query()
                .withOption("mergeObjects", false)
                .withMatch("testdb", "testdoc", "testkey")
                .upsertAsJson(rsAccountInfo);


        System.out.println("Done");

        //connect();
    }
}
