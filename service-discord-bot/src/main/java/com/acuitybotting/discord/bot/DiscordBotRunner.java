package com.acuitybotting.discord.bot;

import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.management.RabbitManagement;
import com.acuitybotting.db.arango.acuity.identities.service.PrincipalLinkService;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.function.Function;

@Component
public class DiscordBotRunner implements CommandLineRunner {

    @Value("${discord.token}")
    private String discordToken;

    @Value("${rabbit.host}")
    private String host;
    @Value("${rabbit.username}")
    private String username;
    @Value("${rabbit.password}")
    private String password;

    private final PrincipalLinkService linkService;

    @Autowired
    public DiscordBotRunner(PrincipalLinkService linkService) {
        this.linkService = linkService;
    }

    @Override
    public void run(String... args) throws Exception {
        JDA jda = new JDABuilder(AccountType.BOT).setToken(discordToken).buildBlocking();
        jda.addEventListener(new ListenerAdapter(){
            @Override
            public void onMessageReceived(MessageReceivedEvent event) {
                if (event.getAuthor().getId().equals("387430433266335757")) return;

                if (event.getChannel().getId().equals("382526096656302081")){
                    if (event.getMessage().getContentRaw().equals("!count")){
                        try {
                            RabbitManagement.loadAll("http://" + host + ":" + "15672", username, password);
                            long count = RabbitManagement.getConnections().values().stream().map(Collection::stream).flatMap(Function.identity()).count();
                            event.getChannel().sendMessage(new StringBuilder().append("Connected: " + count)).queue();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (event.getMessage().getContentRaw().startsWith("!register ")){
                    String jwt = event.getMessage().getContentRaw().replace("!register", "").trim();
                    try {
                        linkService.saveLinkJwt(jwt, "discord", event.getAuthor().getId());
                        event.getMessage().getChannel().sendMessageFormat("Registered.").queue();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println(event.getAuthor().getName() + " in " + event.getChannel().getName() + ": " + event.getMessage().getContentRaw());
            }
        });
    }
}
