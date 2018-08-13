package com.acuitybotting.bot.launcher.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import oshi.json.SystemInfo;
import oshi.json.hardware.HardwareAbstractionLayer;
import oshi.json.json.AbstractOshiJsonObject;
import oshi.json.software.os.OperatingSystem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StateService {

    private SystemInfo si = new SystemInfo();
    private HardwareAbstractionLayer hal = si.getHardware();
    private OperatingSystem os = si.getOperatingSystem();

    public Map<String, Object> buildState(){
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

        return state;
    }
}
