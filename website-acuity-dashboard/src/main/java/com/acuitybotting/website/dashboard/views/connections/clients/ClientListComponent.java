package com.acuitybotting.website.dashboard.views.connections.clients;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.website.dashboard.DashboardRabbitService;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.services.ClientsService;
import com.acuitybotting.website.dashboard.utils.Components;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.web.context.annotation.SessionScope;

@SpringComponent
@SessionScope
public class ClientListComponent extends InteractiveList<GsonRabbitDocument> {

    private final DashboardRabbitService rabbitService;

    public ClientListComponent(ClientsService clientsService, DashboardRabbitService rabbitService) {
        this.rabbitService = rabbitService;

        withColumn("ID", "33%", document -> new Span(), (document, span) -> span.setText(document.getSubKey()));
        withColumn("", "33%", document -> Components.button(VaadinIcon.CLOSE, event -> clientsService.kill(document.getSubKey())), (document, button) -> {
        });
        withLoad(GsonRabbitDocument::getSubKey, clientsService::loadClients);
    }
}