package com.acuitybotting.security.influxdb;

/**
 * Created by Zachary Herridge on 6/6/2018.
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication()
@ComponentScan("com.acuitybotting")
public class InfluxDbAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfluxDbAuthApplication.class, args);
    }
}
