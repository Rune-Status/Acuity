package com.acuitybotting.discord.bot.services.rspeer;

import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.management.RabbitManagement;
import com.acuitybotting.db.arango.acuity.identities.domain.Principal;
import com.acuitybotting.db.arango.acuity.identities.service.PrincipalLinkService;
import com.acuitybotting.discord.bot.DiscordBotService;
import com.acuitybotting.discord.bot.services.rabbit.DiscordBotRabbitService;
import com.arangodb.springframework.core.ArangoOperations;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 8/6/2018.
 */
@Service
@Slf4j
public class RsPeerMessageHandler {

    private final ArangoOperations arangoOperations;
    private final PrincipalLinkService linkService;
    private final DiscordBotRabbitService rabbitService;
    private final DiscordBotService discordBotService;

    @Autowired
    public RsPeerMessageHandler(ArangoOperations arangoOperations, PrincipalLinkService linkService, DiscordBotRabbitService rabbitService, DiscordBotService discordBotService) {
        this.arangoOperations = arangoOperations;
        this.linkService = linkService;
        this.rabbitService = rabbitService;
        this.discordBotService = discordBotService;
    }

    private Set<Principal> getRsPeerPrincipals(String uid) {
        return linkService.findLinksContaining(uid).stream().filter(principal -> "rspeer".equals(principal.getType())).collect(Collectors.toSet());
    }

    @EventListener
    public void onDiscordMessage(MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith("!")) return;

        if (event.getChannel().getId().equals("382526096656302081") || event.getAuthor().getId().equals("161503770503544832")){
            if (event.getMessage().getContentRaw().startsWith("!report")){
                String report = RsPeerUserReportGenerator.mapToString(RsPeerUserReportGenerator.generateAll(arangoOperations));
                System.out.println(report);
            }
        }

        if (event.getMessage().getContentRaw().startsWith("!setup")){
            discordBotService.sendMessage(event.getChannel(), "Go to rspeer client open the menu press \"copy jwt\" and private message me !register YOUR_JWT_HERE").queue();
        }

        if (event.getMessage().getContentRaw().startsWith("!register ")) {
            String jwt = event.getMessage().getContentRaw().replace("!register", "").trim();

            if (!event.isFromType(ChannelType.PRIVATE)) event.getMessage().delete().reason("Exposing their jwt.").queue();

            try {
                linkService.saveLinkJwt(jwt, "discord", event.getAuthor().getId());
                discordBotService.sendMessage(event.getMessage().getChannel(), "Successfully linked the given account to your discord.").queue();
            } catch (UnsupportedEncodingException e) {
                log.error("Failed to link accounts.", e);
            }

            return;
        }

        Set<Principal> rsPeerPrincipals = getRsPeerPrincipals(event.getMessage().getAuthor().getId());
        if (rsPeerPrincipals.size() == 0) {
            discordBotService.sendMessage(event.getMessage().getChannel(), "You must link your discord to your RsPeer account to use commands. Run !setup for more information.").queue();
            return;
        }

        if (event.getMessage().getContentRaw().equals("!count")) {
            rabbitService.loadAll();
            for (Principal principal : rsPeerPrincipals) {
                int size = RabbitManagement.getConnections().getOrDefault(principal.getUid(), Collections.emptyList()).size();
                discordBotService.sendMessage(event.getMessage().getChannel(), "You have {} client(s) connected to Acuity.", size).queue();
            }
        }
    }
}
