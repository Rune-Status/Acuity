package com.acuitybotting.statistics;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by Zachary Herridge on 8/21/2018.
 */
@SpringBootApplication()
@EnableScheduling
@ComponentScan(value = "com.acuitybotting")
public class StatisticsApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(StatisticsApplication.class);
        builder.run(args);
    }
}
