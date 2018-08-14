package com.acuitybotting.discord.bot.services.rabbit;

import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.RabbitHub;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.management.RabbitManagement;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.data.flow.messaging.services.identity.RoutingUtil;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityUsersService;
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

@Service
@Getter
@Slf4j
public class DiscordBotRabbitService implements CommandLineRunner {

    private final DiscordBotService discordBotService;
    private final AcuityUsersService acuityUsersService;
    private final ApplicationEventPublisher publisher;

    @Value("${rabbit.username}")
    private String username;
    @Value("${rabbit.password}")
    private String password;

    @Autowired
    public DiscordBotRabbitService(DiscordBotService discordBotService, AcuityUsersService acuityUsersService, ApplicationEventPublisher publisher) {
        this.discordBotService = discordBotService;
        this.acuityUsersService = acuityUsersService;
        this.publisher = publisher;
    }

    public void loadAll() {
        try {
            RabbitManagement.loadAll(username, password);
        } catch (Exception e) {
            log.error("Error during loading Rabbit management connections.", e);
        }
    }

    @EventListener
    public void onRabbitMessage(MessageEvent messageEvent) {
        if (messageEvent.getRouting().endsWith("services.discord-bot.sendPm")) {
            acuityUsersService.findUserByUid(RoutingUtil.routeToUserId(messageEvent.getRouting())).ifPresent(user -> {
                user.getLinkedPrincipals().stream().filter(principal -> PrincipalLinkTypes.DISCORD.equals(principal.getType())).forEach(principal -> {
                    discordBotService.getJda().getUserById(principal.getUid()).openPrivateChannel().queue(privateChannel -> discordBotService.sendMessage(privateChannel, messageEvent.getMessage().getBody()).queue());
                });
            });
        }
    }

    private void connect() {
        try {
            RabbitHub rabbitHub = new RabbitHub();
            rabbitHub.auth(username, password);
            rabbitHub.start("ADB");

            rabbitHub.createPool(2, channel -> {
                channel.createQueue("acuitybotting.work.discord-bot", false)
                        .withListener(publisher::publishEvent)
                        .open(false);
            });
        } catch (Throwable e) {
            log.error("Error during dashboard RabbitMQ setup.", e);
        }
    }

    @Override
    public void run(String... strings) throws Exception {
        connect();
    }
}
