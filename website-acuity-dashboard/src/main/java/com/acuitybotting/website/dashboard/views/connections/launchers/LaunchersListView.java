package com.acuitybotting.website.dashboard.views.connections.launchers;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.StringRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.repository.RabbitDocumentRepository;
import com.acuitybotting.website.dashboard.security.view.interfaces.UsersOnly;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.acuitybotting.website.dashboard.views.connections.ConnectionsTabNavComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.Set;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */
@Route(value = "connections/launchers", layout = RootLayout.class)
public class LaunchersListView extends VerticalLayout implements UsersOnly {

    private final RabbitDocumentRepository documentRepository;

    public LaunchersListView(RabbitDocumentRepository documentRepository, ConnectionsTabNavComponent connectionsTabNavComponent) {
        add(connectionsTabNavComponent);
        this.documentRepository = documentRepository;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        Set<StringRabbitDocument> connections = documentRepository.findAllByPrincipalIdAndDatabaseAndSubGroup(getPrincipalUid(), "services.registered-connections", "connections");
        for (StringRabbitDocument connection : connections) {
            if ((boolean) connection.getHeaders().getOrDefault("connected", false)) {
                add(new Span(connection.getSubKey()));
            }
        }
    }
}
