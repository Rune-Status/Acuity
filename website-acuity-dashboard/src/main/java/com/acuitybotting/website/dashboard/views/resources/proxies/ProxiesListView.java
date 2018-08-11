package com.acuitybotting.website.dashboard.views.resources.proxies;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.Proxy;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.utils.Layouts;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.acuitybotting.website.dashboard.views.resources.ResourcesTabsComponent;
import com.google.gson.Gson;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */
@Route(value = "resources/proxies", layout = RootLayout.class)
public class ProxiesListView extends VerticalLayout implements Authed {

    public ProxiesListView(ResourcesTabsComponent resourcesTabsComponent, ProxyListComponent proxyListComponent) {
        setPadding(false);
        add(resourcesTabsComponent, proxyListComponent);
    }

    @SpringComponent
    @UIScope
    private static class ProxyListComponent extends InteractiveList<Proxy> {

        private final RabbitDbService rabbitDbService;

        @Autowired
        private ProxyListComponent(RabbitDbService rabbitDbService) {
            this.rabbitDbService = rabbitDbService;
            withColumn("Host", "20%", document -> new Span(), (document, span) -> span.setText(document.getHost()));
            withColumn("Port", "20%", document -> new Span(), (document, span) -> span.setText(String.valueOf(document.getPort())));
            withColumn("Username", "20%", document -> new Span(), (document, span) -> span.setText(String.valueOf(document.getUsername())));
            withColumn("Password", "20%", document -> new Span(), (document, span) -> span.setText(document.getEncryptedPassword() != null ? "Set" : "None"));
            withColumn("", "10%", proxy -> {
                Button button = new Button(VaadinIcon.EDIT.create());
                button.addClickListener(buttonClickEvent -> getUI().ifPresent(ui -> ui.navigate(ProxyEditView.class, proxy.getSubId())));
                return button;
            }, (proxy, button) -> {});
            withLoad(Proxy::getSubId, this::loadProxies);

            Button add = new Button(VaadinIcon.PLUS_CIRCLE.create());
            getControls().add(add);
            add.addClickListener(buttonClickEvent -> getUI().ifPresent(ui -> ui.navigate(ProxyEditView.class)));
        }

        @Override
        protected void onAttach(AttachEvent attachEvent) {
            load();
        }

        private Set<Proxy> loadProxies() {
            return rabbitDbService
                    .loadByGroup(
                            RabbitDbService.buildQueryMapMultiPrincipal(
                                    Authed.getAllPrincipalsIds(),
                                    "services.bot-control-data.proxies",
                                    "proxy"
                            ),
                            GsonRabbitDocument.class
                    ).stream().map(document -> document.getSubDocumentAs(Proxy.class)).collect(Collectors.toSet());
        }
    }
}
