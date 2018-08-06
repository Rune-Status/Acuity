package com.acuitybotting.discord.bot.services.rabbit;

import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.management.RabbitManagement;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 8/6/2018.
 */
@Service
@Getter
@Slf4j
public class DiscordBotRabbitService {

    @Value("${rabbit.host}")
    private String host;
    @Value("${rabbit.username}")
    private String username;
    @Value("${rabbit.password}")
    private String password;

    public void loadAll() {
        try {
            RabbitManagement.loadAll("http://" + host + ":" + "15672", username, password);
        } catch (Exception e) {
            log.error("Error during loading Rabbit management connections.", e);
        }
    }
}
