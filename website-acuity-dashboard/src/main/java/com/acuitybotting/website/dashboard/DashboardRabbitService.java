package com.acuitybotting.website.dashboard;

import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by Zachary Herridge on 7/18/2018.
 */
@Service
@Slf4j
public class DashboardRabbitService implements CommandLineRunner {

    @Value("${rabbit.host}")
    private String host;

    @Value("${rabbit.username}")
    private String username;

    @Value("${rabbit.password}")
    private String password;

    private final ApplicationEventPublisher publisher;
    private final ConfigurableApplicationContext applicationContext;

    private MessagingChannel messagingChannel;

    @Autowired
    public DashboardRabbitService(ApplicationEventPublisher publisher, ConfigurableApplicationContext applicationContext) {
        this.publisher = publisher;
        this.applicationContext = applicationContext;
    }

    private void connect(){
        try {
            RabbitClient rabbitClient = new RabbitClient();
            rabbitClient.auth(host, username, password);
            rabbitClient.connect("AWD_001_" + UUID.randomUUID().toString());
            messagingChannel = rabbitClient.openChannel();
        }
        catch (Throwable e){
            log.error("Error during dashboard RabbitMQ setup.", e);
        }
    }

    public MessagingChannel getMessagingChannel() {
        return messagingChannel;
    }

    @Override
    public void run(String... strings) throws Exception {
        connect();
    }
}
