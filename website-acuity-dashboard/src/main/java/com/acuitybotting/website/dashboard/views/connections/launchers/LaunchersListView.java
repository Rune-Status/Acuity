package com.acuitybotting.website.dashboard.views.connections.launchers;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.MapRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.repository.RabbitDocumentRepository;
import com.acuitybotting.website.dashboard.DashboardRabbitService;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.security.view.interfaces.UsersOnly;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.acuitybotting.website.dashboard.views.connections.ConnectionsTabNavComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */
@Route(value = "connections/launchers", layout = RootLayout.class)
public class LaunchersListView extends VerticalLayout implements UsersOnly {

    private final RabbitDocumentRepository documentRepository;
    private final DashboardRabbitService rabbitService;

    private InteractiveList<MapRabbitDocument> interactiveList = new InteractiveList<>();

    public LaunchersListView(RabbitDocumentRepository documentRepository, ConnectionsTabNavComponent connectionsTabNavComponent, DashboardRabbitService rabbitService) {
        this.rabbitService = rabbitService;
        this.documentRepository = documentRepository;

        setPadding(false);

        interactiveList.getControls().add(new Button("Launch Client"));
        interactiveList.withColumn("Key", "35%", rabbitDocumentCache -> new Span(), (rabbitDocumentCache, span) -> span.setText(rabbitDocumentCache.getSubKey()));
        interactiveList.withColumn("Value", "35%", rabbitDocumentCache -> new Span(), (rabbitDocumentCache, span) -> span.setText(rabbitDocumentCache.getDatabase()));
        interactiveList.withLoad(
                MapRabbitDocument::getSubKey,
                () -> documentRepository.findAllByPrincipalIdAndDatabaseAndSubGroup(getPrincipalUid(), "services.registered-connections", "connections")
                        .stream()
                        .filter(connection -> connection.getSubKey().startsWith("ABL_") && (boolean) connection.getHeaders().getOrDefault("connected", false))
                        .collect(Collectors.toSet())
        );

        add(connectionsTabNavComponent, interactiveList);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        interactiveList.load();
    }
}
