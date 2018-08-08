package com.acuitybotting.website.dashboard.views.connections;

import com.acuitybotting.website.dashboard.components.general.nav.NavigationTab;
import com.acuitybotting.website.dashboard.views.connections.clients.ClientsListView;
import com.acuitybotting.website.dashboard.views.connections.launchers.LaunchersListView;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */
@SpringComponent
@UIScope
public class ConnectionsTabNavComponent extends Tabs {

    public ConnectionsTabNavComponent() {
        setWidth("100%");

        NavigationTab accounts = new NavigationTab("Clients", ClientsListView.class);
        add(accounts);

        NavigationTab proxies = new NavigationTab("Launchers", LaunchersListView.class);
        add(proxies);

        addSelectedChangeListener(selectedChangeEvent -> ((NavigationTab) getSelectedTab()).navigateTo());
    }

}
