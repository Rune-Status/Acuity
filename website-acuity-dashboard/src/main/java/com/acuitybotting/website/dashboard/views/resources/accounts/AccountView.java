package com.acuitybotting.website.dashboard.views.resources.accounts;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.RsAccountInfo;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.acuitybotting.website.dashboard.utils.Notifications;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Route(value = "resources/accounts", layout = RootLayout.class)
public class AccountView extends VerticalLayout implements HasUrlParameter<String>, Authed {

    private final RabbitDbService rabbitDbService;

    private String accountId;
    private RsAccountInfo account;

    @Autowired
    public AccountView(RabbitDbService rabbitDbService) {
        this.rabbitDbService = rabbitDbService;
    }


    @Override
    protected void onAttach(AttachEvent attachEvent) {
        GsonRabbitDocument proxy = rabbitDbService.loadByKey(getQueryMap(accountId), GsonRabbitDocument.class);
        if (proxy == null) {
            Notifications.error("Failed to find account.");
            return;
        }
        else {
            account = proxy.getSubDocumentAs(RsAccountInfo.class);
        }
    }

    private Map<String, Object> getQueryMap(String accountId){
        return RabbitDbService.buildQueryMap(
                Authentication.getAcuityPrincipalId(),
                "services.player-cache",
                "players",
                accountId
        );
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String accountId) {
        this.accountId = accountId;
    }
}
