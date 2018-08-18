package com.acuitybotting.website.dashboard;

import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.channel.RabbitChannel;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.channel.RabbitChannelPool;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitHub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

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

    private RabbitChannelPool pool;

    @Autowired
    public DashboardRabbitService(ApplicationEventPublisher publisher, ConfigurableApplicationContext applicationContext) {
        this.publisher = publisher;
        this.applicationContext = applicationContext;
    }

    private void connect(){
        try {

            RabbitHub rabbitHub = new RabbitHub();
            rabbitHub.auth(username, password);
            rabbitHub.start("AWD", "1.0.01");
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
        connect();
    }
}
