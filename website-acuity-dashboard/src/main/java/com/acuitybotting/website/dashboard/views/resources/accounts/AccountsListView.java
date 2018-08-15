package com.acuitybotting.website.dashboard.views.resources.accounts;

import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.acuitybotting.website.dashboard.views.resources.ResourcesTabsComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */
@Route(value = "resources/accounts", layout = RootLayout.class)
public class AccountsListView extends VerticalLayout implements Authed {

    public AccountsListView(AccountListComponent accountListComponent, ResourcesTabsComponent resourcesTabsComponent) {
        setPadding(false);
        add(resourcesTabsComponent, accountListComponent);
    }
}
