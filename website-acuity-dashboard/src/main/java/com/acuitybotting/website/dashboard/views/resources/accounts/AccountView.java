package com.acuitybotting.website.dashboard.views.resources.accounts;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.RsAccountInfo;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.components.general.separator.TitleSeparator;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.acuitybotting.website.dashboard.utils.Notifications;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Span;
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

    private InteractiveList<Map.Entry<String, Long>> levelList = new InteractiveList<>();

    @Autowired
    public AccountView(RabbitDbService rabbitDbService) {
        this.rabbitDbService = rabbitDbService;

        levelList.withColumn("Skill", "20%", entry -> new Span(), (entry, span) -> span.setText(entry.getKey()));
        levelList.withColumn("Level", "20%", entry -> new Span(), (entry, span) -> span.setText(String.valueOf(entry.getValue())));
        levelList.withLoadAction(Map.Entry::getKey, this::refresh);
        add(new TitleSeparator("Account Skills"), levelList);

    }
    private void refresh(){
        GsonRabbitDocument account = rabbitDbService.loadByKey(getQueryMap(accountId), GsonRabbitDocument.class);
        if (account == null) {
            Notifications.error("Failed to find account.");
            getUI().ifPresent(ui -> ui.navigate(AccountsListView.class));
            return;
        }

        this.account = account.getSubDocumentAs(RsAccountInfo.class);
        levelList.update(this.account.getLevels().entrySet());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        refresh();
    }

    private Map<String, Object> getQueryMap(String accountId){
        return RabbitDbService.buildQueryMap(
                Authentication.getAcuityPrincipalId(),
                "services.rs-accounts",
                "players",
                accountId
        );
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String accountId) {
        this.accountId = accountId;
    }
}
