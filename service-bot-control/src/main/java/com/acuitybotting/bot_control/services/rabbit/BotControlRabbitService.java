package com.acuitybotting.bot_control.services.rabbit;

import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitHub;
import com.acuitybotting.data.flow.messaging.services.db.domain.RabbitDbRequest;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.data.flow.messaging.services.identity.RabbitUtil;
import com.acuitybotting.db.arango.acuity.identities.domain.Principal;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityUsersService;
import com.acuitybotting.db.arango.acuity.identities.service.PrincipalLinkTypes;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbAccess;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbQueryBuilder;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.UpsertResult;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
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

    private RabbitHub rabbitHub = new RabbitHub();

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
            rabbitHub.auth(username, password);
            rabbitHub.start("ABW", "1.0.01");

            rabbitHub.createPool(1, channel -> {
                try {
                    channel.createQueue("bot-control-worker-" + UUID.randomUUID().toString(), true)
                            .bind("amq.rabbitmq.event", "connection.#")
                            .withListener(publisher::publishEvent)
                            .open(true);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });

            rabbitHub.createPool(10, channel -> {
                channel.createQueue("acuitybotting.work.bot-control", false)
                        .withListener(this::handleRequest)
                        .open(false);
            });
        } catch (Throwable e) {
            log.error("Error during dashboard RabbitMQ setup.", e);
        }
    }

    private void publishUpsert(String userId, RabbitDbRequest request, UpsertResult upsert){
        if (upsert == null) return;
        try {
            Gson gson = new Gson();
            rabbitHub.getLocalPool().getChannel()
                    .createMessage()
                    .setTargetExchange("acuitybotting.general")
                    .setTargetRouting("user." + userId + ".rabbitdb.update." + request.getDatabase() + "." + request.getGroup())
                    .setBody(gson.toJson(upsert))
                    .send();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void handle(MessageEvent messageEvent, RabbitDbRequest request, String userId) {
        //log.info("Handling db request {} for user {}.", request, userId);

        RabbitDbQueryBuilder builder = dbService.query().withMatch(userId, request.getDatabase(), request.getGroup(), request.getKey(), request.getRev());

        Gson gson = new Gson();

        if (RabbitDbAccess.isWriteAccessible(userId, request.getDatabase())) {
            if (request.getType() == RabbitDbRequest.SAVE_UPDATE) {
                UpsertResult upsert = builder.upsert(request.getUpdateDocument(), request.getInsertDocument());
                publishUpsert(userId, request, upsert);
            } else if (request.getType() == RabbitDbRequest.DELETE_BY_KEY && RabbitDbAccess.isDeleteAccessible(userId, request.getDatabase())) {
                builder.delete();
            }
        }

        try {
            if (RabbitDbAccess.isReadAccessible(userId, request.getDatabase())) {
                if (request.getType() == RabbitDbRequest.FIND_BY_KEY) {
                    GsonRabbitDocument gsonRabbitDocument = builder.findOne(GsonRabbitDocument.class).orElse(null);
                    try {
                        messageEvent.getQueue().getChannel().buildResponse(messageEvent.getMessage(), gsonRabbitDocument == null ? "" : gson.toJson(gsonRabbitDocument)).send();
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                } else if (request.getType() == RabbitDbRequest.FIND_BY_GROUP) {
                    Set<GsonRabbitDocument> all = builder.findAll(GsonRabbitDocument.class);
                    messageEvent.getQueue().getChannel().buildResponse(messageEvent.getMessage(), gson.toJson(all)).send();
                }
            }
        } catch (MessagingException e) {
            log.error("Error during response", e);
        }
    }

    public void handleRequest(MessageEvent messageEvent) {
        try {
            if (messageEvent.getRouting().contains("db.handleRequest")) {
                String userId = RabbitUtil.routeToUserId(messageEvent.getRouting());
                try {
                    handle(messageEvent, new Gson().fromJson(messageEvent.getMessage().getBody(), RabbitDbRequest.class), userId);
                    messageEvent.getQueue().getChannel().acknowledge(messageEvent.getMessage());
                }
                catch (Throwable e ){
                    e.printStackTrace();
                }
            }

            if (messageEvent.getRouting().contains(".services.bot-control.getLinkJwt")) {
                String userId = RabbitUtil.routeToUserId(messageEvent.getRouting());
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
