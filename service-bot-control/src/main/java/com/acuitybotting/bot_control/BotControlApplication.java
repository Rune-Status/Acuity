package com.acuitybotting.bot_control;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@SpringBootApplication()
@ComponentScan("com.acuitybotting")
@EnableScheduling
public class BotControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotControlApplication.class, args);
    }
}
