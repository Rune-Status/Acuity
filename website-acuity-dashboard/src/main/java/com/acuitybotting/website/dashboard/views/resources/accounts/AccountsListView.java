package com.acuitybotting.website.dashboard.views.resources.accounts;

import com.acuitybotting.website.dashboard.security.view.interfaces.UsersOnly;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.acuitybotting.website.dashboard.views.resources.ResourcesTabsComponent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */
@Route(value = "resources/accounts", layout = RootLayout.class)
public class AccountsListView extends VerticalLayout implements UsersOnly {

    public AccountsListView(ResourcesTabsComponent resourcesTabsComponent) {
        add(resourcesTabsComponent);
        add(new Span("Accounts"));
    }
}
