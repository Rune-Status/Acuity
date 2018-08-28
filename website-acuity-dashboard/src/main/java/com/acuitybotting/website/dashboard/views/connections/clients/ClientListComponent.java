package com.acuitybotting.website.dashboard.views.connections.clients;

import com.acuitybotting.db.arangodb.repositories.connections.domain.ClientConnection;
import com.acuitybotting.db.arangodb.repositories.connections.service.RegisteredConnectionsService;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.services.ClientsService;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.acuitybotting.website.dashboard.utils.Components;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class ClientListComponent extends InteractiveList<ClientConnection> {

    private final RegisteredConnectionsService connectionsService;

    public ClientListComponent(ClientsService clientsService, RegisteredConnectionsService connectionsService) {
        this.connectionsService = connectionsService;

        withColumn("ID", "33%", document -> new Div(), (document, div) -> div.setText(document.get_key()));
        withColumn("", "33%", document -> Components.button(VaadinIcon.CLOSE, event -> clientsService.kill(document.get_key())), (document, button) -> { });
        withLoad(ClientConnection::get_key, () -> connectionsService.findClientsByType(Authentication.getAcuityPrincipalId(), "bot-client", ClientConnection.class));
    }
}