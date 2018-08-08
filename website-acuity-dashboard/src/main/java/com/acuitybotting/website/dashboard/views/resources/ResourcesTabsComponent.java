package com.acuitybotting.website.dashboard.views.resources;

import com.acuitybotting.website.dashboard.components.general.nav.NavigationTab;
import com.acuitybotting.website.dashboard.views.resources.accounts.AccountsListView;
import com.acuitybotting.website.dashboard.views.resources.proxies.ProxiesListView;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */

@SpringComponent
@UIScope
public class ResourcesTabsComponent extends Tabs {

    public ResourcesTabsComponent() {
        setWidth("100%");

        NavigationTab accounts = new NavigationTab("Accounts", AccountsListView.class);
        add(accounts);

        NavigationTab proxies = new NavigationTab("Proxies", ProxiesListView.class);
        add(proxies);

        addSelectedChangeListener(selectedChangeEvent -> ((NavigationTab) getSelectedTab()).navigateTo());
    }
}
