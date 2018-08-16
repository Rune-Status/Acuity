package com.acuitybotting.website.dashboard.services;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.Proxy;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 8/15/2018.
 */
@UIScope
@Service
public class ProxiesService {

    private final RabbitDbService rabbitDbService;

    public ProxiesService(RabbitDbService rabbitDbService) {
        this.rabbitDbService = rabbitDbService;
    }

    public Set<Proxy> loadProxies() {
        return rabbitDbService
                .loadByGroup(
                        RabbitDbService.buildQueryMap(
                                Authentication.getAcuityPrincipalId(),
                                "services.bot-control-data.proxies",
                                "proxy"
                        ),
                        GsonRabbitDocument.class
                ).stream().map(document -> document.getSubDocumentAs(Proxy.class)).collect(Collectors.toSet());
    }

}
