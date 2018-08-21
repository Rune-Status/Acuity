package com.acuitybotting.website.dashboard.views;

import com.acuitybotting.website.dashboard.components.general.nav.TopMenuComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.shared.ui.Transport;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Zachary Herridge on 6/27/2018.
 */

@BodySize(height = "100vh", width = "100vw")
@Theme(value = Lumo.class, variant = Lumo.DARK)
@StyleSheet("/acuity.css")
@Push(transport = Transport.LONG_POLLING)
@Slf4j
public class RootLayout extends Div implements RouterLayout {

    private VerticalLayout content = new VerticalLayout();

    public RootLayout() {
        UI.getCurrent().getPage().addJavaScript("https://code.jquery.com/jquery-3.1.1.min.js");
        UI.getCurrent().getPage().addJavaScript("https://code.highcharts.com/highcharts.src.js");

        UI.getCurrent().getPage().addJavaScript("/js/acuity-hc-theme.js");
        UI.getCurrent().getPage().addJavaScript("/js/acuity-charts.js");

        setSizeFull();
        getClassNames().add("acuity-root");
        getElement().getStyle().set("overflow", "auto");

        VerticalLayout root = new VerticalLayout();
        root.setSpacing(false);
        root.setMargin(false);
        root.setPadding(false);
        root.getClassNames().add("acuity-container");

        content.getClassNames().add("acuity-content");
        content.setSizeFull();
        content.setMargin(false);
        content.setPadding(false);
        content.setSpacing(false);

        TopMenuComponent topMenuComponent = new TopMenuComponent();
        root.add(content);

        add(topMenuComponent, root);
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        content.getElement().getComponent().ifPresent(component -> {
            this.content.removeAll();
            this.content.add(component);
        });
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        log.info("Root attached.");
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        log.info("Root detached.");
    }
}
