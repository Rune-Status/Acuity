package com.acuitybotting.discord.bot.services.rspeer;

import com.acuitybotting.db.arango.acuity.identities.domain.Principal;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityUsersService;
import com.acuitybotting.db.arango.acuity.identities.service.PrincipalLinkTypes;
import com.acuitybotting.discord.bot.DiscordBotService;
import com.arangodb.springframework.core.ArangoOperations;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RsPeerMessageHandler {

    private final ArangoOperations arangoOperations;

    private final AcuityUsersService acuityUsersService;
    private final DiscordBotService discordBotService;

    @Autowired
    public RsPeerMessageHandler(ArangoOperations arangoOperations, AcuityUsersService acuityUsersService, DiscordBotService discordBotService) {
        this.arangoOperations = arangoOperations;
        this.acuityUsersService = acuityUsersService;
        this.discordBotService = discordBotService;
    }

    @EventListener
    public void onDiscordMessage(MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith("!")) return;

        if (!event.getAuthor().getId().equals("161503770503544832") && !event.isFromType(ChannelType.PRIVATE)){
            return;
        }

        if (event.getMessage().getContentRaw().startsWith("!jwt")) {
            String linkJwt = acuityUsersService.createLinkJwt(Principal.of(PrincipalLinkTypes.DISCORD, event.getAuthor().getId()));
            discordBotService.sendMessage(event.getChannel(), "Your jwt is: {}", linkJwt);
        }
    }
}
