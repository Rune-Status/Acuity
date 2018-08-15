package com.acuitybotting.website.dashboard.services;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.RsAccountInfo;
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
public class AccountsService {

    private final RabbitDbService rabbitDbService;

    public AccountsService(RabbitDbService rabbitDbService) {
        this.rabbitDbService = rabbitDbService;
    }

    public Set<RsAccountInfo> loadAccounts() {
        return rabbitDbService
                .loadByGroup(
                        RabbitDbService.buildQueryMap(
                                Authentication.getAcuityPrincipalId(),
                                "services.rs-accounts",
                                "players"
                        ),
                        GsonRabbitDocument.class
                )
                .stream()
                .map(gsonRabbitDocument -> gsonRabbitDocument.getSubDocumentAs(RsAccountInfo.class))
                .collect(Collectors.toSet());
    }
}
