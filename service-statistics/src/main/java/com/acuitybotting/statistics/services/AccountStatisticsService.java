package com.acuitybotting.statistics.services;

import com.acuitybotting.common.utils.GsonUtil;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.data.flow.messaging.services.identity.RabbitUtil;
import com.acuitybotting.db.arango.acuity.statistic.event.domain.StatisticEvent;
import com.acuitybotting.db.arango.acuity.statistic.event.repository.StatisticEventRepository;
import com.acuitybotting.db.influx.InfluxDbService;
import com.acuitybotting.db.influx.domain.write.Point;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 8/22/2018.
 */
@Service
public class AccountStatisticsService {

    private final StatisticEventRepository eventRepository;
    private final InfluxDbService influxDbService;

    public AccountStatisticsService(StatisticEventRepository eventRepository, InfluxDbService influxDbService) {
        this.eventRepository = eventRepository;
        this.influxDbService = influxDbService;
    }

    @EventListener
    public void onMessage(MessageEvent messageEvent){
        String eventType = messageEvent.getMessage().getAttributes().get("eventType");
        if ("account.banned".equals(eventType) || "account.locked".equals(eventType)) {
            JsonObject body = new Gson().fromJson(messageEvent.getMessage().getBody(), JsonObject.class);
            String accountLogin = body.get("accountLogin").getAsString();
            if (eventRepository.existsByTypeAndKey(eventType, accountLogin)) return;

            StatisticEvent event = new StatisticEvent();
            event.setType(eventType);
            event.setKey(accountLogin);
            eventRepository.save(event);

            Point point = new Point();
            point.setMeasurement(event.getType());
            point.getTags().put("principalId", RabbitUtil.routeToUserId(messageEvent.getRouting()));
            point.getTags().put("clientName", GsonUtil.getOrDefault(body.get("clientName"), "rspeer"));
            point.getFields().put("count", 1);
            influxDbService.write("rs-account-stats", point);
        }
    }
}
