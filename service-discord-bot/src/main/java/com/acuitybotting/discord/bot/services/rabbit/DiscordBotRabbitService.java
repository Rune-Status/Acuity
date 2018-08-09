/*
package com.acuitybotting.discord.bot.services.rabbit;

import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.management.RabbitManagement;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.data.flow.messaging.services.identity.RoutingUtil;
import com.acuitybotting.db.arango.acuity.identities.domain.Principal;
import com.acuitybotting.db.arango.acuity.identities.service.PrincipalLinkTypes;
import com.acuitybotting.discord.bot.DiscordBotService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

*/
/**
 * Created by Zachary Herridge on 8/6/2018.
 *//*

@Service
@Getter
@Slf4j
public class DiscordBotRabbitService implements CommandLineRunner {


    private final DiscordBotService discordBotService;
    private final ApplicationEventPublisher publisher;

    @Value("${rabbit.host}")
    private String host;
    @Value("${rabbit.username}")
    private String username;
    @Value("${rabbit.password}")
    private String password;

    @Autowired
    public DiscordBotRabbitService(PrincipalLinkService linkService, DiscordBotService discordBotService, ApplicationEventPublisher publisher) {
        this.linkService = linkService;
        this.discordBotService = discordBotService;
        this.publisher = publisher;
    }

    public void loadAll() {
        try {
            RabbitManagement.loadAll("http://" + host + ":" + "15672", username, password);
        } catch (Exception e) {
            log.error("Error during loading Rabbit management connections.", e);
        }
    }

    @EventListener
    public void onRabbitMessage(MessageEvent messageEvent) {
        if (messageEvent.getRouting().endsWith("services.discord-bot.sendPm")) {
            String uid = RoutingUtil.routeToUserId(messageEvent.getRouting());
            for (Principal principal : getDiscordPrincipals(uid)) {
                discordBotService.getJda().getUserById(principal.getUid()).openPrivateChannel().queue(privateChannel -> discordBotService.sendMessage(privateChannel, messageEvent.getMessage().getBody()).queue());
            }
        }
    }

    private Set<Principal> getDiscordPrincipals(String uid) {
        return linkService.findLinksContaining(Principal.of(PrincipalLinkTypes.RSPEER, uid)).stream().filter(principal -> PrincipalLinkTypes.DISCORD.equals(principal.getType())).collect(Collectors.toSet());
    }

    private void connect() {
        try {
            RabbitClient rabbitClient = new RabbitClient();
            rabbitClient.auth(host, username, password);
            rabbitClient.connect("ADB_" + UUID.randomUUID().toString());
            MessagingChannel channel = rabbitClient.openChannel();

            channel.createQueue("acuitybotting.work.discord-bot", false)
                    .withListener(publisher::publishEvent)
                    .open(false);
        } catch (Throwable e) {
            log.error("Error during dashboard RabbitMQ setup.", e);
        }
    }

    @Override
    public void run(String... strings) throws Exception {
        connect();
    }
}
*/
