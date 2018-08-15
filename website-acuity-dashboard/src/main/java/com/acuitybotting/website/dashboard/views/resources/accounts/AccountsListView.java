package com.acuitybotting.website.dashboard.views.resources.accounts;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.RsAccountInfo;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.services.AccountsService;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.acuitybotting.website.dashboard.views.resources.ResourcesTabsComponent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.annotation.SessionScope;

import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */
@Route(value = "resources/accounts", layout = RootLayout.class)
public class AccountsListView extends VerticalLayout implements Authed {

    public AccountsListView(AccountListComponent accountListComponent, ResourcesTabsComponent resourcesTabsComponent) {
        setPadding(false);
        add(resourcesTabsComponent, accountListComponent);
    }

    @SpringComponent
    @SessionScope
    private static class AccountListComponent extends InteractiveList<RsAccountInfo> {

        @Autowired
        public AccountListComponent(AccountsService accountsService) {
            withColumn("Email", "33%", document -> {
                Span span = new Span();
                span.getElement().addEventListener("click", domEvent -> getUI().ifPresent(ui -> ui.navigate(AccountView.class, document.getSubKey())));
                return span;
            }, (document, span) -> span.setText(document.getSubKey()));
            withColumn("Last World", "33%", document -> new Span(), (document, span) -> span.setText(String.valueOf(document.getWorld())));
            withLoad(RsAccountInfo::getSubKey, accountsService::loadAccounts);
        }
    }
}
