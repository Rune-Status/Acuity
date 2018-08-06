package com.acuitybotting.discord.bot.services.rspeer;

import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.management.RabbitManagement;
import com.acuitybotting.db.arango.acuity.identities.domain.Principal;
import com.acuitybotting.db.arango.acuity.identities.service.PrincipalLinkService;
import com.acuitybotting.discord.bot.services.rabbit.DiscordBotRabbitService;
import lombok.extern.slf4j.Slf4j;
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

    private final PrincipalLinkService linkService;
    private final DiscordBotRabbitService rabbitService;

    @Autowired
    public RsPeerMessageHandler(PrincipalLinkService linkService, DiscordBotRabbitService rabbitService) {
        this.linkService = linkService;
        this.rabbitService = rabbitService;
    }

    private Set<Principal> getRsPeerPrincipals(String uid) {
        return linkService.findLinksContaining(uid).stream().filter(principal -> "rspeer".equals(principal.getType())).collect(Collectors.toSet());
    }

    @EventListener
    public void onDiscordMessage(MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith("!")) return;

        if (event.getMessage().getContentRaw().startsWith("!register ")) {
            String jwt = event.getMessage().getContentRaw().replace("!register", "").trim();
            try {
                linkService.saveLinkJwt(jwt, "discord", event.getAuthor().getId());
                event.getMessage().getChannel().sendMessageFormat("Successfully linked the given account to your discord.").queue();
            } catch (UnsupportedEncodingException e) {
                log.error("Failed to link accounts.", e);
            }

            return;
        }

        Set<Principal> rsPeerPrincipals = getRsPeerPrincipals(event.getMessage().getAuthor().getId());
        if (rsPeerPrincipals.size() == 0){
            event.getMessage().getChannel().sendMessageFormat("You must link your discord to your RsPeer account to use commands.").queue();
            return;
        }

        if (event.getMessage().getContentRaw().equals("!count")) {
            rabbitService.loadAll();
            for (Principal principal : rsPeerPrincipals) {
                int size = RabbitManagement.getConnections().getOrDefault(principal.getUid(), Collections.emptyList()).size();
                event.getChannel().sendMessage("You have ").append(String.valueOf(size)).append(" clients connected to Acuity.").queue();
            }
        }
    }
}
