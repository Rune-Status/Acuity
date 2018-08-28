package com.acuitybotting.website.dashboard.views.connections.launchers;

import com.acuitybotting.db.arangodb.repositories.connections.domain.LauncherConnection;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.components.general.separator.TitleSeparator;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.services.LaunchersService;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Zachary Herridge on 8/14/2018.
 */
@Route(value = "connections/launchers/deploy", layout = RootLayout.class)
public class LaunchClientsView extends VerticalLayout implements Authed {

    private final LaunchClientsComponent launchClientsComponent;
    private final LauncherSelectComponent launcherListComponent;

    @Autowired
    public LaunchClientsView(LaunchClientsComponent launchClientsComponent, LauncherSelectComponent launcherListComponent) {
        this.launchClientsComponent = launchClientsComponent;
        this.launcherListComponent = launcherListComponent;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (attachEvent.isInitialAttach()){
            setPadding(false);
            add(new TitleSeparator("Selected Launchers"), launcherListComponent);
            add(launchClientsComponent);
        }
    }

    @SpringComponent
    @UIScope
    public static class LauncherSelectComponent extends InteractiveList<LauncherConnection> implements Authed {

        public LauncherSelectComponent(LaunchersService launchersService) {
            withSelectionEnabled();
            withColumn("ID", "33%", document -> new Div(), (document, div) -> div.setText(document.getParent().getSubKey()));
            withColumn("Username", "25%", document -> new Div(), (document, div) -> div.setText(document.getState().getUserName()));
            withSearchable(launcherConnection -> launcherConnection.getParent().getSubKey() + " " + launcherConnection.getState().getUserName());
            withLoad(launcherConnection -> launcherConnection.getParent().getSubKey(), launchersService::loadLaunchers);
        }
    }
}
