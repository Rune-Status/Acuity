package com.acuitybotting.website.dashboard.services;

import com.acuitybotting.data.flow.messaging.services.db.implementations.rabbit.RabbitDb;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.RsAccountInfo;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Optional;
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

    public Optional<RsAccountInfo> findAccount(String accountEmail) {
        return rabbitDbService.loadByKey(
                RabbitDbService.buildQueryMap(
                        Authentication.getAcuityPrincipalId(),
                        "services.rs-accounts",
                        "players",
                        accountEmail
                ),
                GsonRabbitDocument.class
        ).map(gsonRabbitDocument -> gsonRabbitDocument.getSubDocumentAs(RsAccountInfo.class));
    }

    public boolean save(String accountEmail, String encryptedPassword) {
        if (Strings.isNullOrEmpty(accountEmail) || Strings.isNullOrEmpty(encryptedPassword)) return false;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("encryptedPassword", encryptedPassword);
        String json = jsonObject.toString();

        rabbitDbService.save(
                RabbitDb.STRATEGY_UPDATE,
                RabbitDbService.buildQueryMap(
                        Authentication.getAcuityPrincipalId(),
                        "services.rs-accounts",
                        "players",
                        accountEmail
                ),
                null,
                json,
                json
        );

        return true;
    }
}
