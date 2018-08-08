package com.acuitybotting.website.dashboard.views.resources;

import com.acuitybotting.website.dashboard.components.general.nav.NavigationTab;
import com.acuitybotting.website.dashboard.views.resources.accounts.AccountsListView;
import com.vaadin.flow.component.tabs.Tabs;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */

public class ResourcesTabsComponent extends Tabs {

    public ResourcesTabsComponent() {
        setWidth("100%");

        NavigationTab accounts = new NavigationTab("Accounts", AccountsListView.class);
        add(accounts);

        NavigationTab proxies = new NavigationTab("Proxies", AccountsListView.class);
        add(proxies);

        addSelectedChangeListener(selectedChangeEvent -> ((NavigationTab) getSelectedTab()).navigateTo());
    }
}
