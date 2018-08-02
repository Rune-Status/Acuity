package com.acuitybotting.discord.bot;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DiscordBotRunner implements CommandLineRunner {

    @Value("${discord.token}")
    private String discordToken;

    @Override
    public void run(String... args) throws Exception {
        JDA jda = new JDABuilder(AccountType.BOT).setToken(discordToken).buildBlocking();
        jda.addEventListener(new ListenerAdapter(){
            @Override
            public void onMessageReceived(MessageReceivedEvent event) {
                System.out.println(event);
            }
        });
    }
}
