package com.acuitybotting.statistics.services;

import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.db.influx.InfluxDbService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 8/22/2018.
 */
@Service
public class AccountStatisticsService {

    private final InfluxDbService influxDbService;
    private final RsBuddyService rsBuddyService;

    public AccountStatisticsService(InfluxDbService influxDbService, RsBuddyService rsBuddyService) {
        this.influxDbService = influxDbService;
        this.rsBuddyService = rsBuddyService;
    }

    @EventListener
    public void onMessage(MessageEvent messageEvent) {

    }
}
