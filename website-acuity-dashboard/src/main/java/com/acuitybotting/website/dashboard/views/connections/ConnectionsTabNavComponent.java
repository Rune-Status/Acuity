package com.acuitybotting.website.dashboard.views.connections;

import com.acuitybotting.website.dashboard.components.general.nav.tabs.NavigationTab;
import com.acuitybotting.website.dashboard.components.general.nav.tabs.NavigationTabs;
import com.acuitybotting.website.dashboard.views.connections.clients.ClientsListView;
import com.acuitybotting.website.dashboard.views.connections.launchers.LaunchersListView;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */
@SpringComponent
@UIScope
public class ConnectionsTabNavComponent extends NavigationTabs {

    public ConnectionsTabNavComponent() {
        super();
        withTab(new NavigationTab("Clients", ClientsListView.class));
        withTab(new NavigationTab("Launchers", LaunchersListView.class));
    }
}
