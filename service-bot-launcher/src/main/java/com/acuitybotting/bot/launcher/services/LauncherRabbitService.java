package com.acuitybotting.bot.launcher.services;


import com.acuitybotting.bot.launcher.enviroments.RSPeerEnviroment;
import com.acuitybotting.bot.launcher.utils.CommandLine;
import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.data.flow.messaging.services.client.utils.RabbitHub;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import oshi.json.SystemInfo;
import oshi.json.hardware.HardwareAbstractionLayer;
import oshi.json.json.AbstractOshiJsonObject;
import oshi.json.software.os.OperatingSystem;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 8/6/2018.
 */
@Service
@Slf4j
public class LauncherRabbitService implements CommandLineRunner {

    private RabbitHub rabbitHub = new RabbitHub();

    private void connect() {
        try {
            rabbitHub.start("ABL", RSPeerEnviroment.getSession(), 1);
            rabbitHub.createLocalQueue(true)
                    .withListener(this::handleMessage)
                    .open(true);
        } catch (Throwable e) {
            log.error("Error during dashboard RabbitMQ setup.", e);
        }
    }

    @Scheduled(initialDelay = 5000, fixedDelay = 60000)
    private void updateState(){
        try {
            SystemInfo si = new SystemInfo();
            HardwareAbstractionLayer hal = si.getHardware();
            OperatingSystem os = si.getOperatingSystem();

            Map<String, Object> state = new HashMap<>();

            state.put("processes", Arrays.stream(os.getProcesses(5, oshi.software.os.OperatingSystem.ProcessSort.MEMORY)).map(AbstractOshiJsonObject::toJSON).collect(Collectors.toList()));

            state.put("cpuLoad", hal.getProcessor().getSystemCpuLoad());
            state.put("cpuUpTime", hal.getProcessor().getSystemUptime());
            state.put("cpuTemp", hal.getSensors().getCpuTemperature());

            state.put("halMemoryAvailable", hal.getMemory().getAvailable());
            state.put("memoryTotal", hal.getMemory().getTotal());

            state.put("javaHome", System.getProperty("java.home"));
            state.put("javaVendor", System.getProperty("java.vendor"));
            state.put("javaVersionUrl", System.getProperty("java.vendor.url"));
            state.put("javaVersion", System.getProperty("java.version"));

            state.put("osArch", System.getProperty("os.arch"));
            state.put("osName", System.getProperty("os.name"));
            state.put("osVersion", System.getProperty("os.version"));

            state.put("userName", System.getProperty("user.name"));
            state.put("userHome", System.getProperty("user.home"));
            state.put("userDir", System.getProperty("user.dir"));

            rabbitHub.updateConnectionDocument(new Gson().toJson(Collections.singletonMap("state", state)));
            log.info("Updated state.");
        } catch (MessagingException e) {
            log.error("Error updating state.", e);
        }
    }

    private void handleMessage(MessageEvent messageEvent) {
        if ("runCommand".equals(messageEvent.getMessage().getAttributes().get("type"))) {
            JsonElement launchConfig = new Gson().fromJson(messageEvent.getMessage().getBody(), JsonElement.class);
            log.info("Got launch config: ", launchConfig);
            String command = CommandLine.replacePlaceHolders(launchConfig.getAsJsonObject().get("command").getAsString());

            log.info("Running command: {}", command);

            try {
                CommandLine.runCommand(command);
            } catch (Throwable e) {
                log.error("Error running command.", e);
            }
        }
    }

    @Override
    public void run(String... strings) throws Exception {
        connect();
    }
}
