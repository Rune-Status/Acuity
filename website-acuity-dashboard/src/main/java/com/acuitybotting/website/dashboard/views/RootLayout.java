package com.acuitybotting.website.dashboard.views;

import com.acuitybotting.website.dashboard.components.general.nav.TopMenuComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * Created by Zachary Herridge on 6/27/2018.
 */

@BodySize(height = "100vh", width = "100vw")
@Theme(value = Lumo.class, variant = Lumo.DARK)
@JavaScript("https://code.highcharts.com/highcharts.src.js")
@JavaScript("https://code.jquery.com/jquery-3.3.1.min.js")
@StyleSheet("/acuity.css")
public class RootLayout extends Div implements RouterLayout {

    private VerticalLayout content = new VerticalLayout();

    public RootLayout() {
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
        super.onAttach(attachEvent);
        System.out.println("Root attached");
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        System.out.println("Root detached");
    }
}
