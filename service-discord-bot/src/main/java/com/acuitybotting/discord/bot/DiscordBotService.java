package com.acuitybotting.discord.bot;

import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.restaction.MessageAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Getter
public class DiscordBotService implements CommandLineRunner {

    @Value("${discord.token}")
    private String discordToken;

    private final ApplicationEventPublisher publisher;
    private JDA jda;

    @Autowired
    public DiscordBotService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public MessageAction sendMessage(MessageChannel channel, String message, Object... objects){
        if (objects != null){
            for (Object object : objects) {
                message = message.replaceFirst("\\{}", String.valueOf(object));
            }
        }

        return channel.sendMessage(message);
    }

    @Override
    public void run(String... args) throws Exception {
        jda = new JDABuilder(AccountType.BOT).setToken(discordToken).addEventListener(new ListenerAdapter() {
            @Override
            public void onMessageReceived(MessageReceivedEvent event) {
                if (event.getAuthor().getId().equals("387430433266335757")) return;
                publisher.publishEvent(event);
            }
        }).build();
    }
}
