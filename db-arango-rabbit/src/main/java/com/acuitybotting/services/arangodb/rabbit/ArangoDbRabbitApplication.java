package com.acuitybotting.services.arangodb.rabbit;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@EnableScheduling
@ComponentScan("com.acuitybotting")
public class ArangoDbRabbitApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ArangoDbRabbitApplication.class);
        builder.run(args);
    }
}
