package com.acuitybotting.bot_control.services.rabbit;

import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.db.domain.RabbitDbRequest;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.data.flow.messaging.services.identity.RoutingUtil;
import com.acuitybotting.db.arango.acuity.identities.domain.Principal;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityUsersService;
import com.acuitybotting.db.arango.acuity.identities.service.PrincipalLinkTypes;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
@Service
@Slf4j
public class BotControlRabbitService implements CommandLineRunner {

    private final AcuityUsersService acuityUsersService;
    private final ApplicationEventPublisher publisher;

    private final RabbitDbService dbService;

    @Value("${rabbit.host}")
    private String host;
    @Value("${rabbit.username}")
    private String username;
    @Value("${rabbit.password}")
    private String password;

    @Autowired
    public BotControlRabbitService(AcuityUsersService acuityUsersService, ApplicationEventPublisher publisher, RabbitDbService dbService) {
        this.acuityUsersService = acuityUsersService;
        this.publisher = publisher;
        this.dbService = dbService;
    }

    private void connect() {
        try {
            RabbitClient rabbitClient = new RabbitClient();
            rabbitClient.auth(host, username, password);
            rabbitClient.connect("ABW_" + UUID.randomUUID().toString());

            rabbitClient.openChannel().createQueue("bot-control-worker-" + UUID.randomUUID().toString(), true)
                    .bind("amq.rabbitmq.event", "connection.#")
                    .withListener(publisher::publishEvent)
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

    public void handle(MessageEvent messageEvent, RabbitDbRequest request, String userId) {
        //log.info("Handling db request {} for user {}.", request, userId);

        Map<String, Object> queryMap = RabbitDbService.buildQueryMap(userId, request.getDatabase(), request.getGroup(), request.getKey(), request.getRev());

        Gson gson = new Gson();

        if (dbService.isWriteAccessible(userId, request.getDatabase())) {
            if (request.getType() == RabbitDbRequest.SAVE_REPLACE || request.getType() == RabbitDbRequest.SAVE_UPDATE) {
                dbService.save(request.getType(), queryMap, null, request.getUpdateDocument(), request.getInsertDocument());
            } else if (request.getType() == RabbitDbRequest.DELETE_BY_KEY && dbService.isDeleteAccessible(userId, request.getDatabase())) {
                dbService.delete(queryMap);
            }
        }

        try {
            if (dbService.isReadAccessible(userId, request.getDatabase())) {
                if (request.getType() == RabbitDbRequest.FIND_BY_KEY) {
                    GsonRabbitDocument gsonRabbitDocument = dbService.loadByKey(queryMap, GsonRabbitDocument.class);
                    try {
                        messageEvent.getQueue().getChannel().buildResponse(messageEvent.getMessage(), gsonRabbitDocument == null ? "" : gson.toJson(gsonRabbitDocument)).send();
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                } else if (request.getType() == RabbitDbRequest.FIND_BY_GROUP) {
                    messageEvent.getQueue().getChannel().buildResponse(messageEvent.getMessage(), gson.toJson(dbService.loadByGroup(queryMap, GsonRabbitDocument.class))).send();
                }
            }
        } catch (MessagingException e) {
            log.error("Error during response", e);
        }
    }

    public void handleRequest(MessageEvent messageEvent) {
        try {
            if (messageEvent.getRouting().contains("db.handleRequest")) {
                String userId = RoutingUtil.routeToUserId(messageEvent.getRouting());
                try {
                    handle(messageEvent, new Gson().fromJson(messageEvent.getMessage().getBody(), RabbitDbRequest.class), userId);
                    messageEvent.getQueue().getChannel().acknowledge(messageEvent.getMessage());
                }
                catch (Throwable e ){
                    e.printStackTrace();
                }
            }

            if (messageEvent.getRouting().contains(".services.bot-control.getLinkJwt")) {
                String userId = RoutingUtil.routeToUserId(messageEvent.getRouting());
                try {
                    messageEvent.getQueue().getChannel().buildResponse(messageEvent.getMessage(), acuityUsersService.createLinkJwt(Principal.of(PrincipalLinkTypes.RSPEER, userId))).send();
                } catch (MessagingException e) {
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
