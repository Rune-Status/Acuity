package com.acuitybotting.bot_control.services.rabbit;

import com.acuitybotting.bot_control.services.user.db.RabbitDbService;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.MessagingClient;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.RabbitChannel;
import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.client.listeners.adapters.ChannelListenerAdapter;
import com.acuitybotting.data.flow.messaging.services.client.listeners.adapters.ClientListenerAdapter;
import com.acuitybotting.data.flow.messaging.services.db.domain.RabbitDbRequest;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.data.flow.messaging.services.identity.RoutingUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
@Service
@Slf4j
public class BotControlRabbitService implements CommandLineRunner {

    private final ApplicationEventPublisher publisher;
    private final RabbitDbService dbService;

    @Value("${rabbit.host}")
    private String host;
    @Value("${rabbit.username}")
    private String username;
    @Value("${rabbit.password}")
    private String password;

    private RabbitChannel rabbitChannel;

    @Autowired
    public BotControlRabbitService(ApplicationEventPublisher publisher, RabbitDbService dbService) {
        this.publisher = publisher;
        this.dbService = dbService;
    }

    private void connect() {
        try {
            RabbitClient rabbitClient = new RabbitClient();
            rabbitClient.auth(host, username, password);

            rabbitClient.getListeners().add(new ClientListenerAdapter() {
                @Override
                public void onConnect(MessagingClient client) {
                    rabbitChannel = (RabbitChannel) client.createChannel();
                    rabbitChannel.getListeners().add(new ChannelListenerAdapter() {
                        @Override
                        public void onConnect(MessagingChannel channel) {
                            String localQueue = "bot-control-worker-" + UUID.randomUUID().toString();

                            try {
                                channel.getQueue(localQueue)
                                        .create()
                                        .withListener(publisher::publishEvent)
                                        .bind("amq.rabbitmq.event", "queue.#")
                                        .consume(true);

                                channel.getQueue("acuitybotting.work.acuity-db.request")
                                        .withListener(publisher::publishEvent)
                                        .consume(false);

                            } catch (MessagingException e) {
                                log.error("Error during queue setup.", e);
                            }
                        }

                        @Override
                        public void onShutdown(MessagingChannel channel, Throwable cause) {
                            channel.connect();
                        }
                    });

                    rabbitChannel.connect();
                }
            });

            rabbitClient.connect("ABW_001_" + UUID.randomUUID().toString());
        } catch (Throwable e) {
            log.error("Error during dashboard RabbitMQ setup.", e);
        }
    }

    @EventListener
    public void handleScriptStorageRequest(MessageEvent messageEvent) {
        if (messageEvent.getRouting().contains(".services.acuity-db.request")) {
            String userId = RoutingUtil.routeToUserId(messageEvent.getRouting());
            dbService.handle(messageEvent, new Gson().fromJson(messageEvent.getMessage().getBody(), RabbitDbRequest.class), userId);
            try {
                messageEvent.getQueue().getChannel().acknowledge(messageEvent.getMessage());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run(String... strings) throws Exception {
        connect();
    }
}
