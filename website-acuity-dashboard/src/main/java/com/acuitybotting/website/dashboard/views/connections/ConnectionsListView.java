package com.acuitybotting.website.dashboard.views.connections;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.StringRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.repository.RabbitDocumentRepository;
import com.acuitybotting.website.dashboard.security.view.interfaces.UsersOnly;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@SpringComponent
@Route(value = "connections", layout = RootLayout.class)
public class ConnectionsListView extends VerticalLayout implements UsersOnly {

    private final RabbitDocumentRepository documentRepository;

    @Autowired
    public ConnectionsListView(RabbitDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        Set<StringRabbitDocument> connections = documentRepository.findAllByPrincipalIdAndDatabaseAndSubGroup(getPrincipalUid(), "registered-connections", "connections");
        for (StringRabbitDocument connection : connections) {
            add(new Span(connection.getSubKey()));
        }
    }
}
