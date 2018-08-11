package com.acuitybotting.website.dashboard.components.general.separator;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Getter;

@Getter
public class TitleSeparator extends HorizontalLayout {

    private Span titleSpan = new Span();
    private HorizontalLayout controls = new HorizontalLayout();

    public TitleSeparator(String title) {
        setWidth("100%");
        setPadding(false);
        controls.setPadding(false);
        getClassNames().add("acuity-title-separator");

        titleSpan.setText(title);
        titleSpan.setWidth("95%");
        titleSpan.getStyle().set("font-size", "21px");

        add(titleSpan, controls);
    }
}
