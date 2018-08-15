package com.acuitybotting.website.dashboard.views.connections.launchers;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.LauncherConnection;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.services.LaunchersService;
import com.acuitybotting.website.dashboard.utils.Components;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class LauncherListComponent extends InteractiveList<LauncherConnection> implements Authed{

    public LauncherListComponent(LaunchersService launchersService) {
        getControls().add(Components.button("Launch Client(s)", event -> getUI().ifPresent(ui -> ui.navigate(LaunchClientsView.class))));
        withColumn("ID", "33%", document -> new Span(), (document, span) -> span.setText(document.getSubKey()));
        withColumn("Username", "25%", document -> new Span(), (document, span) -> span.setText(document.getState().getUserName()));
        withColumn("CPU", "10%", document -> new Span(), (document, span) -> span.setText(String.valueOf(document.getState().getCpuLoad())));
        withLoad(LauncherConnection::getSubKey, launchersService::loadLaunchers);
    }
}