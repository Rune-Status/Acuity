package com.acuitybotting.website.dashboard.services;

import com.acuitybotting.data.flow.messaging.services.db.implementations.rabbit.RabbitDb;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.RsAccountInfo;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 8/15/2018.
 */
@Service
@UIScope
public class AccountsService {


    private final RabbitDbService rabbitDbService;

    public AccountsService(RabbitDbService rabbitDbService) {
        this.rabbitDbService = rabbitDbService;
    }

    public Set<RsAccountInfo> loadAccounts() {
        return rabbitDbService
                .queryByGroup()
                .withMatch(Authentication.getAcuityPrincipalId(), "services.rs-accounts", "players")
                .findAll(RsAccountInfo.class);
    }

    public Optional<RsAccountInfo> findAccount(String accountEmail) {
        return rabbitDbService.queryByKey()
                .withMatch(Authentication.getAcuityPrincipalId(), "services.rs-accounts", "players", accountEmail)
                .findOne(RsAccountInfo.class);
    }

    public boolean save(String accountEmail, String encryptedPassword) {
        if (Strings.isNullOrEmpty(accountEmail) || Strings.isNullOrEmpty(encryptedPassword)) return false;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("encryptedPassword", encryptedPassword);
        String json = jsonObject.toString();

        rabbitDbService.query()
                .withMatch(Authentication.getAcuityPrincipalId(), "services.rs-accounts", "players", accountEmail)
                .upsert(json);

        return true;
    }
}
