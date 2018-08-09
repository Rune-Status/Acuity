package com.acuitybotting.website.dashboard.components.general.list_display;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class InteractiveListRow extends HorizontalLayout {

    private Checkbox selectionBox = new Checkbox();
    private InteractiveList list;

    public InteractiveListRow(InteractiveList parent) {
        this.list = parent;
        setMargin(false);
        setWidth("100%");
        getClassNames().add("acuity-interactive-list-row");
        add(selectionBox);
    }

    public InteractiveList getList() {
        return list;
    }
}
