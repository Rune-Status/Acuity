package com.acuitybotting.website.dashboard;

import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitChannel;
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

    private RabbitChannel rabbitChannel;

    @Autowired
    public DashboardRabbitService(ApplicationEventPublisher publisher, ConfigurableApplicationContext applicationContext) {
        this.publisher = publisher;
        this.applicationContext = applicationContext;
    }

    private void connect(){
        try {
            RabbitClient rabbitClient = new RabbitClient();
            rabbitClient.auth(host, username, password);

/*            rabbitClient.getListeners().add(new ClientListenerAdapter(){
                @Override
                public void onConnect(MessagingClient client) {
       *//*             rabbitChannel = (RabbitChannel) client.openChannel();
                    rabbitChannel.getListeners().add(new ChannelListenerAdapter(){
                        @Override
                        public void onConnect(MessagingChannel channel) {
                            try {
                                channel.createQueue("testQueue")
                                        .withListener(messageEvent -> RootLayout.getGlobalEventBus().post(messageEvent.getMessage()))
                                        .create()
                                        .bind("amq.rabbitmq.event", "queue.#")
                                        .bind("acuitybotting.general", "user.*.services.bot-control.set-status")
                                        .consume(true);
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    rabbitChannel.open();*//*
                }
            });*/

            rabbitClient.connect("ADB_" + UUID.randomUUID().toString());
        }
        catch (Throwable e){
            log.error("Error during dashboard RabbitMQ setup.", e);
        }
    }

    @Override
    public void run(String... strings) throws Exception {
        connect();
    }
}
