package com.acuitybotting.website.dashboard.views.resources.accounts;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.RabbitDocumentBase;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.acuitybotting.website.dashboard.views.resources.ResourcesTabsComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */
@Route(value = "resources/accounts", layout = RootLayout.class)
public class AccountsListView extends VerticalLayout implements Authed {

    private AccountListComponent accountListComponent;

    public AccountsListView(AccountListComponent accountListComponent, ResourcesTabsComponent resourcesTabsComponent) {
        this.accountListComponent = accountListComponent;
        setPadding(false);
        add(resourcesTabsComponent, accountListComponent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        accountListComponent.load();
    }

    @SpringComponent
    @UIScope
    private static class AccountListComponent extends InteractiveList<AccountListComponent.RsAccountDocument> {

        private final RabbitDbService rabbitDbService;

        public AccountListComponent(RabbitDbService rabbitDbService) {
            this.rabbitDbService = rabbitDbService;
            withColumn("Email", "33%", document -> new Span(), (document, span) -> span.setText(document.getSubKey()));
            withColumn("Last World", "33%", document -> new Span(), (document, span) -> span.setText(String.valueOf(document.getSubDocument().getWorld())));
            withLoad(RsAccountDocument::getSubKey, this::loadAccounts);
        }

        private Set<RsAccountDocument> loadAccounts() {
            return rabbitDbService.loadByGroup(RabbitDbService.buildQueryMapMultiPrincipal(Authed.getAllPrincipalsIds(), "services.player-cache", "players"), RsAccountDocument.class);
        }

        @Getter
        @ToString
        public static class RsAccountDocument extends RabbitDocumentBase {

            private RsAccountInfo subDocument;

            @Getter
            @ToString
            public static class RsAccountInfo {
                private int world;
            }
        }
    }
}
