package com.acuitybotting.website.dashboard.views.connections.launchers;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.LauncherConnection;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.acuitybotting.website.dashboard.utils.Components;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.acuitybotting.website.dashboard.views.connections.ConnectionsTabNavComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */
@Route(value = "connections/launchers", layout = RootLayout.class)
public class LaunchersListView extends VerticalLayout implements Authed {

    private LauncherListComponent launcherListComponent;

    public LaunchersListView(LauncherListComponent launcherListComponent, ConnectionsTabNavComponent connectionsTabNavComponent) {
        this.launcherListComponent = launcherListComponent;
        setPadding(false);
        add(connectionsTabNavComponent, launcherListComponent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        launcherListComponent.load();
    }

    @SpringComponent
    @UIScope
    private static class LauncherListComponent extends InteractiveList<LauncherConnection> {

        private final RabbitDbService rabbitDbService;

        public LauncherListComponent(RabbitDbService rabbitDbService) {
            this.rabbitDbService = rabbitDbService;

            getControls().add(Components.button("Launch Client(s)", event -> launchClients()));
            withColumn("ID", "33%", document -> new Span(), (document, span) -> span.setText(document.getSubKey()));
            withColumn("Username", "25%", document -> new Span(), (document, span) -> span.setText(document.getState().getUserName()));
            withColumn("CPU", "10%", document -> new Span(), (document, span) -> span.setText(String.valueOf(document.getState().getCpuLoad())));
            withLoad(LauncherConnection::getSubKey, this::loadLaunchers);
        }

        private void launchClients() {
            String collect = Base64.getEncoder().encodeToString(getSelectedValues().map(LauncherConnection::getSubKey).collect(Collectors.joining(",")).getBytes());
            getUI().ifPresent(ui -> ui.navigate(LaunchClientsView.class, collect));
        }

        private Set<LauncherConnection> loadLaunchers() {
            return rabbitDbService
                    .loadByGroup(RabbitDbService.buildQueryMap(Authentication.getAcuityPrincipalId(), "services.registered-connections", "connections"), GsonRabbitDocument.class)
                    .stream()
                    .filter(connection -> connection.getSubKey().startsWith("ABL_") && (boolean) connection.getHeaders().getOrDefault("connected", false))
                    .map(gsonRabbitDocument -> gsonRabbitDocument.getSubDocumentAs(LauncherConnection.class))
                    .collect(Collectors.toSet());
        }
    }
}
