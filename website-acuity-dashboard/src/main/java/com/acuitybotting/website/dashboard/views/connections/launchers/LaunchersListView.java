package com.acuitybotting.website.dashboard.views.connections.launchers;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.LauncherConnection;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.services.LaunchersService;
import com.acuitybotting.website.dashboard.utils.Components;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.acuitybotting.website.dashboard.views.connections.ConnectionsTabNavComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

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
        if (attachEvent.isInitialAttach()) {
            setPadding(false);
            add(connectionsTabNavComponent, launcherListComponent);
        }
    }

    @SpringComponent
    @UIScope
    public static class LauncherListComponent extends InteractiveList<LauncherConnection> implements Authed {

        public LauncherListComponent(LaunchersService launchersService) {
            getControls().add(Components.button("Launch Client(s)", event -> getUI().ifPresent(ui -> ui.navigate(LaunchClientsView.class))));
            withColumn("ID", "33%", document -> new Div(), (document, div) -> div.setText(document.getParent().getSubKey()));
            withColumn("Username", "25%", document -> new Div(), (document, div) -> div.setText(document.getState().getUserName()));
            withColumn("CPU", "10%", document -> new Div(), (document, div) -> div.setText(document.getState().getFormattedCpuLoad()));
            withSearchable(launcherConnection -> launcherConnection.getParent().getSubKey() + " " + launcherConnection.getState().getUserName());
            withLoad(launcherConnection -> launcherConnection.getParent().getSubKey(), launchersService::loadLaunchers);
        }
    }
}
