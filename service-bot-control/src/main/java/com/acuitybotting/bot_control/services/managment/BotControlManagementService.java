package com.acuitybotting.bot_control.services.managment;

import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.management.RabbitManagement;
import com.acuitybotting.data.flow.messaging.services.client.implementation.rabbit.management.domain.RabbitConnection;
import com.acuitybotting.data.flow.messaging.services.db.domain.RabbitDbRequest;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.data.flow.messaging.services.identity.RabbitUtil;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.db.influx.InfluxDbService;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Service
@Slf4j
public class BotControlManagementService {

    private final InfluxDbService influxDbService;
    private final RabbitDbService rabbitDbService;

    @Value("${rabbit.username}")
    private String username;
    @Value("${rabbit.password}")
    private String password;

    @Autowired
    public BotControlManagementService(InfluxDbService influxDbService, RabbitDbService rabbitDbService) {
        this.influxDbService = influxDbService;
        this.rabbitDbService = rabbitDbService;
    }

    private void updateRegisteredConnections() {
        for (Map.Entry<String, Map<String, List<RabbitConnection>>> byUser : RabbitManagement.getConnections().entrySet()) {
            for (Map.Entry<String, List<RabbitConnection>> byType : byUser.getValue().entrySet()) {
                Point build = Point.measurement("connections-count")
                        .addField("count", byType.getValue().size())
                        .tag("principalId", byUser.getKey())
                        .tag("type", byType.getKey())
                        .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                        .build();
                influxDbService.writeAsync(build);

                for (RabbitConnection rabbitConnection : byType.getValue()) {
                    if (rabbitConnection.getUser_provided_name() == null) continue;

                    Map<String, Object> headers = new HashMap<>();
                    headers.put("connected", true);
                    headers.put("connectionTime", rabbitConnection.getConnected_at());
                    headers.put("connectionConfirmationTime", System.currentTimeMillis());
                    headers.put("peerHost", rabbitConnection.getPeer_host());
                    Map<String, Object> map = RabbitDbService.buildQueryMap(byUser.getKey(), "services.registered-connections", "connections", rabbitConnection.getUser_provided_name(), null);
                    rabbitDbService.save(
                            RabbitDbRequest.SAVE_UPDATE,
                            map,
                            headers,
                            null,
                            null
                    );
                }
            }
        }

        String updateTimeout = "FOR r IN RabbitDocument\n" +
                "FILTER r.headers.connectionConfirmationTime != NULL\n" +
                "FILTER r.headers.connectionConfirmationTime < @timeout\n" +
                "UPDATE { _key: r._key, headers: { connected : false}} IN RabbitDocument";
        rabbitDbService.getArangoOperations().query(updateTimeout, Collections.singletonMap("timeout", System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(11)), null, null);
    }

    @Scheduled(fixedDelay = 4000)
    public void updateConnections() {
        try {
            RabbitManagement.loadAll(username, password);
            updateRegisteredConnections();
        } catch (Exception e) {
            log.error("Error during RabbitManagement.loadAll.", e);
        }
    }

    @EventListener
    public void handleRequest(MessageEvent messageEvent) {
        if (messageEvent.getRouting().equals("connection.created")) {
            updateConnections();
        }

        if (messageEvent.getRouting().equals("connection.closed")) {
            String userProvidedName = messageEvent.getMessage().getAttributes().get("header.user_provided_name");
            if (userProvidedName == null) return;

            String singleUpdate =
                    "FOR r IN RabbitDocument\n" +
                            "FILTER r.database == 'services.registered-connections'\n" +
                            "FILTER r.subGroup == 'connections'\n" +
                            "FILTER r.subKey == @userDefinedName\n" +
                            "UPDATE { _key: r._key, headers: { connected : false}} IN RabbitDocument";
            rabbitDbService.getArangoOperations().query(singleUpdate, Collections.singletonMap("userDefinedName", userProvidedName), null, null);
        }
    }
}
