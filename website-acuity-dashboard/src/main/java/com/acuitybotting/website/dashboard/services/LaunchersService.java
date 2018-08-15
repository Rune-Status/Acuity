package com.acuitybotting.website.dashboard.services;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.LauncherConnection;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.website.dashboard.utils.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 8/15/2018.
 */
@Service
@SessionScope
public class LaunchersService {

    private final RabbitDbService rabbitDbService;

    public LaunchersService(RabbitDbService rabbitDbService) {
        this.rabbitDbService = rabbitDbService;
    }

    public Set<LauncherConnection> loadLaunchers() {
        return rabbitDbService
                .loadByGroup(RabbitDbService.buildQueryMap(Authentication.getAcuityPrincipalId(), "services.registered-connections", "connections"), GsonRabbitDocument.class)
                .stream()
                .filter(connection -> connection.getSubKey().startsWith("ABL_") && (boolean) connection.getHeaders().getOrDefault("connected", false))
                .map(gsonRabbitDocument -> gsonRabbitDocument.getSubDocumentAs(LauncherConnection.class))
                .collect(Collectors.toSet());
    }
}
