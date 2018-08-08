package com.acuitybotting.website.dashboard.views.resources.proxies;

import com.acuitybotting.website.dashboard.views.RootLayout;
import com.acuitybotting.website.dashboard.views.resources.ResourcesTabsComponent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */
@Route(value = "resources/proxies", layout = RootLayout.class)
@SpringComponent
@UIScope
public class ProxiesListView extends VerticalLayout {

    public ProxiesListView() {
        add(new ResourcesTabsComponent());
        add(new Span("Proxies"));
    }
}
