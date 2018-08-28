package com.acuitybotting.website.dashboard.views.resources.accounts;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arangodb.repositories.resources.accounts.domain.RsAccount;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.components.general.separator.TitleSeparator;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.acuitybotting.website.dashboard.utils.Notifications;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Route(value = "resources/accounts", layout = RootLayout.class)
public class AccountView extends VerticalLayout implements HasUrlParameter<String>, Authed {

    private final RabbitDbService rabbitDbService;

    private String accountId;
    private RsAccount account;

    private InteractiveList<Map.Entry<String, Long>> levelList = new InteractiveList<>();

    private InteractiveList<Map.Entry<Integer, Integer>> inventoryList = new InteractiveList<>();
    private InteractiveList<Map.Entry<Integer, Integer>> bankList = new InteractiveList<>();

    @Autowired
    public AccountView(RabbitDbService rabbitDbService) {
        this.rabbitDbService = rabbitDbService;

        levelList.withColumn("Skill", "20%", entry -> new Div(), (entry, div) -> div.setText(entry.getKey()));
        levelList.withColumn("Level", "20%", entry -> new Div(), (entry, div) -> div.setText(String.valueOf(entry.getValue())));
        levelList.withLoadAction(Map.Entry::getKey, this::refresh);
        levelList.withSearchable(Map.Entry::getKey);
        add(new TitleSeparator("Skills"), levelList);

        inventoryList.withColumn("Item", "20%", entry -> new Div(), (entry, div) -> div.setText(String.valueOf(entry.getKey())));
        inventoryList.withColumn("Count", "20%", entry -> new Div(), (entry, div) -> div.setText(String.valueOf(entry.getValue())));
        inventoryList.withLoadAction(entry -> String.valueOf(entry.getKey()), this::refresh);
        add(new TitleSeparator("Inventory"), inventoryList);

        bankList.withColumn("Item", "20%", entry -> new Div(), (entry, div) -> div.setText(String.valueOf(entry.getKey())));
        bankList.withColumn("Count", "20%", entry -> new Div(), (entry, div) -> div.setText(String.valueOf(entry.getValue())));
        bankList.withLoadAction(entry -> String.valueOf(entry.getKey()), this::refresh);
        add(new TitleSeparator("Bank"), bankList);
    }
    private void refresh(){
        RsAccount account = rabbitDbService.queryByKey()
                .withMatch(Authentication.getAcuityPrincipalId(), "services.rs-accounts", "players", accountId)
                .findOne(RsAccount.class).orElse(null);

        if (account == null) {
            getUI().ifPresent(ui -> ui.navigate(AccountsListView.class));
            Notifications.error("Failed to find account.");
            return;
        }

        this.account = account;
        levelList.update(this.account.getLevels().entrySet());
        inventoryList.update(this.account.getInventory().entrySet());
        bankList.update(this.account.getBank().entrySet());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        refresh();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String accountId) {
        this.accountId = accountId;
    }
}
