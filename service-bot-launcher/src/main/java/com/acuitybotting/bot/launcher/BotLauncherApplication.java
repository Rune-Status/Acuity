package com.acuitybotting.bot.launcher;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Zachary Herridge on 8/6/2018.
 */
@SpringBootApplication()
@ComponentScan("com.acuitybotting")
public class BotLauncherApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(BotLauncherApplication.class);
        builder.headless(false);
        builder.run(args);
    }

}
