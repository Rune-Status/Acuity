package com.acuitybotting.bot_control.services.rabbit;

import com.acuitybotting.bot_control.services.user.db.RabbitDbService;
import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitChannel;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.db.domain.RabbitDbRequest;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.data.flow.messaging.services.identity.RoutingUtil;
import com.acuitybotting.db.arango.acuity.identities.service.PrincipalLinkService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
@Service
@Slf4j
public class BotControlRabbitService implements CommandLineRunner {

    private final ApplicationEventPublisher publisher;

    private final RabbitDbService dbService;
    private final PrincipalLinkService linkService;

    @Value("${rabbit.host}")
    private String host;
    @Value("${rabbit.username}")
    private String username;
    @Value("${rabbit.password}")
    private String password;

    @Autowired
    public BotControlRabbitService(ApplicationEventPublisher publisher, RabbitDbService dbService, PrincipalLinkService linkService) {
        this.publisher = publisher;
        this.dbService = dbService;
        this.linkService = linkService;
    }

    private void connect() {
        try {
            RabbitClient rabbitClient = new RabbitClient();
            rabbitClient.auth(host, username, password);
            rabbitClient.connect("ABW_" + UUID.randomUUID().toString());

            rabbitClient.openChannel().createQueue("bot-control-worker-" + UUID.randomUUID().toString(), true)
                    .bind("amq.rabbitmq.event", "connection.#")
                    .withListener(this::handleRequest)
                    .open(true);

            for (int i = 0; i < 10; i++) {
                MessagingChannel channel = rabbitClient.openChannel();
                channel.createQueue("acuitybotting.work.bot-control", false)
                        .withListener(this::handleRequest)
                        .open(false);
            }



        } catch (Throwable e) {
            log.error("Error during dashboard RabbitMQ setup.", e);
        }
    }

    public void handleRequest(MessageEvent messageEvent) {
        try {
            if (messageEvent.getRouting().contains("rabbit-db.handleRequest")) {
                String userId = RoutingUtil.routeToUserId(messageEvent.getRouting());
                try {
                    dbService.handle(messageEvent, new Gson().fromJson(messageEvent.getMessage().getBody(), RabbitDbRequest.class), userId);
                    messageEvent.getQueue().getChannel().acknowledge(messageEvent.getMessage());
                }
                catch (Throwable e ){
                    e.printStackTrace();
                }
            }

            if (messageEvent.getRouting().contains(".services.bot-control.getLinkJwt")) {
                String userId = RoutingUtil.routeToUserId(messageEvent.getRouting());
                try {
                    messageEvent.getQueue().getChannel().respond(messageEvent.getMessage(), linkService.createLinkJwt("rspeer", userId));
                } catch (UnsupportedEncodingException | MessagingException e) {
                    log.error("Error in services.bot-control.getLinkJwt", e);
                }
            }
        }
        catch (Throwable e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... strings) throws Exception {
        connect();
    }
}
