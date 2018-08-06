package com.acuitybotting.discord.bot.services.rspeer;

import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.management.RabbitManagement;
import com.acuitybotting.db.arango.acuity.identities.domain.Principal;
import com.acuitybotting.db.arango.acuity.identities.service.PrincipalLinkService;
import com.acuitybotting.discord.bot.services.rabbit.DiscordBotRabbitService;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Collections;

/**
 * Created by Zachary Herridge on 8/6/2018.
 */
@Service
public class RsPeerMessageHandler {

    private final PrincipalLinkService linkService;
    private final DiscordBotRabbitService rabbitService;

    @Autowired
    public RsPeerMessageHandler(PrincipalLinkService linkService, DiscordBotRabbitService rabbitService) {
        this.linkService = linkService;
        this.rabbitService = rabbitService;
    }

    @EventListener
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.getMessage().getContentRaw().equals("!count")) {
            for (Principal principal : linkService.findLinksContaining(event.getMessage().getAuthor().getId())) {
                if (principal.getType().equals("rspeer")) {
                    rabbitService.loadAll();
                    int size = RabbitManagement.getConnections().getOrDefault(principal.getUid(), Collections.emptyList()).size();
                    event.getChannel().sendMessage(new StringBuilder().append("You have " + size + " connections.")).queue();
                }
            }
        }

        if (event.getMessage().getContentRaw().startsWith("!register ")) {
            String jwt = event.getMessage().getContentRaw().replace("!register", "").trim();
            try {
                linkService.saveLinkJwt(jwt, "discord", event.getAuthor().getId());
                event.getMessage().getChannel().sendMessageFormat("Registered.").queue();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
