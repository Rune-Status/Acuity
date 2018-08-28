package com.acuitybotting.statistics.services;

import com.acuitybotting.db.influx.InfluxDbService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 8/21/2018.
 */
@Service
public class ConnectionsStatisticsService {

    private final InfluxDbService influxDbService;

    public ConnectionsStatisticsService(InfluxDbService influxDbService) {
        this.influxDbService = influxDbService;
    }

    @Scheduled(initialDelay = 3000, fixedRate = 10000)
    private void update() {

    }
}
