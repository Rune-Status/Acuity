package com.acuitybotting.website.dashboard.views.resources.accounts;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.MapRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.repository.RabbitDocumentRepository;
import com.acuitybotting.website.dashboard.DashboardRabbitService;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.security.view.interfaces.UsersOnly;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.acuitybotting.website.dashboard.views.connections.ConnectionsTabNavComponent;
import com.acuitybotting.website.dashboard.views.resources.ResourcesTabsComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */
@Route(value = "resources/accounts", layout = RootLayout.class)
public class AccountsListView extends VerticalLayout implements UsersOnly {

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
    private static class AccountListComponent extends InteractiveList<MapRabbitDocument> {

        public AccountListComponent(RabbitDocumentRepository documentRepository, DashboardRabbitService rabbitService) {
            withColumn("Email", "33%", document -> new Span(), (document, span) -> span.setText(document.getSubKey()));
            withLoad(
                    MapRabbitDocument::getSubKey,
                    () -> documentRepository.findAllByPrincipalIdAndDatabaseAndSubGroup(UsersOnly.getCurrentPrincipalUid(), "services.player-cache", "players")
            );
        }
    }
}
