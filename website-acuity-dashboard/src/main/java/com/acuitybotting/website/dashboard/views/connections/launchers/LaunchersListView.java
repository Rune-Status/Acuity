package com.acuitybotting.website.dashboard.views.connections.launchers;

import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.MapRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.repository.RabbitDocumentRepository;
import com.acuitybotting.db.arango.acuity.rabbit_db.util.RabbitDocumentCache;
import com.acuitybotting.website.dashboard.DashboardRabbitService;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveListRow;
import com.acuitybotting.website.dashboard.security.view.interfaces.UsersOnly;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.acuitybotting.website.dashboard.views.connections.ConnectionsTabNavComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
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
    private final DashboardRabbitService rabbitService;

    private InteractiveList interactiveList = new InteractiveList();

    public LaunchersListView(RabbitDocumentRepository documentRepository, ConnectionsTabNavComponent connectionsTabNavComponent, DashboardRabbitService rabbitService) {
        this.rabbitService = rabbitService;
        this.documentRepository = documentRepository;

        setPadding(false);

        interactiveList.getControls().add(new Button("Launch Client"));

        interactiveList.withLoadFuncation(list -> {
            Set<MapRabbitDocument> connections = documentRepository.findAllByPrincipalIdAndDatabaseAndSubGroup(getPrincipalUid(), "services.registered-connections", "connections");
            for (MapRabbitDocument connection : connections) {
                if (connection.getSubKey().startsWith("ABL_") && (boolean) connection.getHeaders().getOrDefault("connected", false)) {
                    list.withRow(new LauncherRow(list, new RabbitDocumentCache(documentRepository, connection)));
                }
            }
        });

        add(connectionsTabNavComponent, interactiveList);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        interactiveList.load();
    }

    public class LauncherRow extends InteractiveListRow {

        private RabbitDocumentCache rabbitDocumentCache;

        public LauncherRow(InteractiveList parent, RabbitDocumentCache rabbitDocumentCache) {
            super(parent);
            this.rabbitDocumentCache = rabbitDocumentCache;
            update();

            add(new Span(rabbitDocumentCache.getSubKey()));

            getElement().addEventListener("click", domEvent -> {
                try {
                    rabbitService.getMessagingChannel().send(
                            "",
                            "user."  + getPrincipalUid() + ".queue." + rabbitDocumentCache.getSubKey(),
                            "{\"rabbitTag\":0,\"body\":\"{\\\"settings\\\":{\\\"rsaccount.email\\\":\\\"testemail.test.com\\\",\\\"rsaccount.password\\\":\\\"testpassword123\\\"}}\"}"
                            );
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
        }

        private void update(){
            if (rabbitDocumentCache.updateCache() == null) return;
        }
    }
}
