package com.acuitybotting.statistics.services;

import com.acuitybotting.db.arangodb.repositories.connections.domain.ClientConnection;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.db.influx.InfluxDbService;
import com.acuitybotting.db.influx.domain.write.Point;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 8/21/2018.
 */
@Service
public class ConnectionsStatisticsService {

    private final InfluxDbService influxDbService;
    private final RabbitDbService rabbitDbService;

    public ConnectionsStatisticsService(InfluxDbService influxDbService, RabbitDbService rabbitDbService) {
        this.influxDbService = influxDbService;
        this.rabbitDbService = rabbitDbService;
    }

    @Scheduled(initialDelay = 3000, fixedRate = 10000)
    private void update() {
        String updateTimeout =
                "FOR r IN @@collection\n" +
                        "FILTER r.subDocument.connected == true\n" +
                        "RETURN r";

        Set<ClientConnection> all =
                rabbitDbService.query(updateTimeout)
                        .findAll(ClientConnection.class)
                        .stream().filter(clientConnection -> clientConnection.getState() != null)
                        .collect(Collectors.toSet());


        long loggedInCount = all.stream().filter(clientConnection -> clientConnection.getState().isLoggedIn()).count();
        long runningScript = all.stream().filter(clientConnection -> clientConnection.getState().getScriptSelector() != null).count();
        long localScript = all.stream().filter(clientConnection -> clientConnection.getState().isLocalScript()).count();

        Point clientStatePoint = new Point();
        clientStatePoint.setMeasurement("clients-state");
        clientStatePoint.getFields().put("count", all.size());
        clientStatePoint.getFields().put("loggedIn", loggedInCount);
        clientStatePoint.getFields().put("runningScript", runningScript);
        clientStatePoint.getFields().put("localScript", localScript);
        influxDbService.write("client-state", clientStatePoint);

        Map<String, List<ClientConnection>> scriptUse = all.stream().filter(clientConnection -> clientConnection.getState().getScriptSelector() != null).collect(Collectors.groupingBy(clientConnection -> clientConnection.getState().getScriptSelector()));
        for (Map.Entry<String, List<ClientConnection>> entry : scriptUse.entrySet()) {
            Point scriptUsePoint = new Point();
            scriptUsePoint.setMeasurement("script-use");
            scriptUsePoint.getFields().put("count", entry.getValue().size());
            scriptUsePoint.getTags().put("selector", entry.getKey());
            influxDbService.write("script-use", scriptUsePoint);
        }
    }
}
