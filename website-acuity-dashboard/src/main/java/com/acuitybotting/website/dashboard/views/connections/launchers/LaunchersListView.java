package com.acuitybotting.website.dashboard.views.connections.launchers;

import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.db.arango.acuity.identities.domain.Principal;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.website.dashboard.DashboardRabbitService;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.acuitybotting.website.dashboard.views.connections.ConnectionsTabNavComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.Collection;
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
    private static class LauncherListComponent extends InteractiveList<GsonRabbitDocument> {

        private final RabbitDbService rabbitDbService;
        private final DashboardRabbitService rabbitService;

        public LauncherListComponent(RabbitDbService rabbitDbService, DashboardRabbitService rabbitService) {
            this.rabbitDbService = rabbitDbService;
            this.rabbitService = rabbitService;
            Button launchClient = new Button("Launch Client");
            launchClient.addClickListener(buttonClickEvent -> launchClients());
            getControls().add(launchClient);
            withColumn("ID", "33%", document -> new Span(), (document, span) -> span.setText(document.getSubKey()));
            withColumn("Host", "15%", document -> new Span(), (document, span) -> span.setText(String.valueOf(document.getHeaders().getOrDefault("peerHost", ""))));
            withColumn("Last Update", "33%", document -> new Span(), (document, span) -> span.setText(String.valueOf(document.getHeaders().getOrDefault("connectionConfirmationTime", ""))));
            withLoad(GsonRabbitDocument::getSubKey, this::loadLaunchers);
        }

        private void launchClients() {
            getSelectedValues().forEach(document -> {
                String queue = "user." + document.getPrincipalId() + ".queue." + document.getSubKey();
                try {
                    rabbitService.getMessagingChannel().buildMessage(
                            "",
                            queue,
                            "{\"rabbitTag\":0,\"body\":\"{\\\"settings\\\":{\\\"rsaccount.email\\\":\\\"testemail.test.com\\\",\\\"rsaccount.password\\\":\\\"testpassword123\\\"}}\"}"
                    ).setAttribute("type", "startClient").send();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
        }

        private Set<GsonRabbitDocument> loadLaunchers() {
            return rabbitDbService
                    .loadByGroup(RabbitDbService.buildQueryMapMultiPrincipal(Authed.getAllPrincipalsIds(), "services.registered-connections", "connections"), GsonRabbitDocument.class)
                    .stream()
                    .filter(connection -> connection.getSubKey().startsWith("ABL_") && (boolean) connection.getHeaders().getOrDefault("connected", false))
                    .collect(Collectors.toSet());
        }
    }
}
