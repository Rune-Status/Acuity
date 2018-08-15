package com.acuitybotting.website.dashboard.views.connections.launchers;

import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.acuitybotting.website.dashboard.views.connections.ConnectionsTabNavComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */
@Route(value = "connections/launchers", layout = RootLayout.class)
public class LaunchersListView extends VerticalLayout implements Authed {

    private final LauncherListComponent launcherListComponent;
    private final ConnectionsTabNavComponent connectionsTabNavComponent;

    public LaunchersListView(LauncherListComponent launcherListComponent, ConnectionsTabNavComponent connectionsTabNavComponent) {
        this.launcherListComponent = launcherListComponent;
        this.connectionsTabNavComponent = connectionsTabNavComponent;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (attachEvent.isInitialAttach()){
            setPadding(false);
            add(connectionsTabNavComponent, launcherListComponent);
        }
    }
}
