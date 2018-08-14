package com.acuitybotting.website.dashboard.views.connections.launchers;

import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.RabbitDocumentBase;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.website.dashboard.DashboardRabbitService;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.acuitybotting.website.dashboard.utils.Components;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.acuitybotting.website.dashboard.views.connections.ConnectionsTabNavComponent;
import com.google.gson.Gson;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.*;
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
    private static class LauncherListComponent extends InteractiveList<GsonRabbitDocument> {

        private final RabbitDbService rabbitDbService;

        public LauncherListComponent(RabbitDbService rabbitDbService) {
            this.rabbitDbService = rabbitDbService;

            getControls().add(Components.button("Launch Client(s)", event -> launchClients()));
            withColumn("ID", "33%", document -> new Span(), (document, span) -> span.setText(document.getSubKey()));
            withColumn("Host", "15%", document -> new Span(), (document, span) -> span.setText(String.valueOf(document.getHeaders().getOrDefault("peerHost", ""))));
            withColumn("Last Update", "33%", document -> new Span(), (document, span) -> span.setText(String.valueOf(document.getHeaders().getOrDefault("connectionConfirmationTime", ""))));
            withLoad(GsonRabbitDocument::getSubKey, this::loadLaunchers);
        }

        private void launchClients() {
            String collect = Base64.getEncoder().encodeToString(getSelectedValues().map(RabbitDocumentBase::getSubKey).collect(Collectors.joining(",")).getBytes());
            getUI().ifPresent(ui -> ui.navigate(LaunchClientsView.class, collect));
        }

        private Set<GsonRabbitDocument> loadLaunchers() {
            return rabbitDbService
                    .loadByGroup(RabbitDbService.buildQueryMap(Authentication.getAcuityPrincipalId(), "services.registered-connections", "connections"), GsonRabbitDocument.class)
                    .stream()
                    .filter(connection -> connection.getSubKey().startsWith("ABL_") && (boolean) connection.getHeaders().getOrDefault("connected", false))
                    .collect(Collectors.toSet());
        }
    }
}
