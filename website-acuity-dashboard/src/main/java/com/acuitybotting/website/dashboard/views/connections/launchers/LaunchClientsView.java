package com.acuitybotting.website.dashboard.views.connections.launchers;

import com.acuitybotting.website.dashboard.components.general.separator.TitleSeparator;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Zachary Herridge on 8/14/2018.
 */
@Route(value = "connections/launchers/deploy", layout = RootLayout.class)
public class LaunchClientsView extends VerticalLayout implements Authed {

    private final LaunchClientsComponent launchClientsComponent;
    private final LauncherListComponent launcherListComponent;

    @Autowired
    public LaunchClientsView(LaunchClientsComponent launchClientsComponent, LauncherListComponent launcherListComponent) {
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
}
