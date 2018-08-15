package com.acuitybotting.website.dashboard.views.resources;

import com.acuitybotting.website.dashboard.components.general.nav.tabs.NavigationTab;
import com.acuitybotting.website.dashboard.components.general.nav.tabs.NavigationTabs;
import com.acuitybotting.website.dashboard.views.resources.accounts.AccountsListView;
import com.acuitybotting.website.dashboard.views.resources.proxies.ProxiesListView;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */

@SpringComponent
@UIScope
public class ResourcesTabsComponent extends NavigationTabs {

    public ResourcesTabsComponent() {
        super();
        withTab(new NavigationTab("Accounts", AccountsListView.class));
        withTab(new NavigationTab("Proxies", ProxiesListView.class));
    }
}
