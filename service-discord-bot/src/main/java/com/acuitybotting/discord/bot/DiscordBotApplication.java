package com.acuitybotting.discord.bot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@ComponentScan("com.acuitybotting")
public class DiscordBotApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(DiscordBotApplication.class);
        builder.run(args);
    }
}
