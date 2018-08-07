package com.acuitybotting.path_finding;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by Zachary Herridge on 5/31/2018.
 */
@SpringBootApplication()
@EnableScheduling
@ComponentScan(value = "com.acuitybotting")
public class PathFindingApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(PathFindingApplication.class);
        builder.headless(false);
        builder.run(args);
    }
}
