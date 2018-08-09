package com.acuitybotting.website.dashboard.components.general.list_display;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class InteractiveListRow<T> extends HorizontalLayout {

    private Checkbox selectionBox = new Checkbox();
    private InteractiveList<T> list;
    private Map<String, Object> columnComponents = new HashMap<>();

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

    @SuppressWarnings("unchecked")
    public void update(T value) {
        for (InteractiveListColumn column : list.getColumns()) {
            Object component = columnComponents.computeIfAbsent(column.getUid(), s -> {
                Component apply = (Component) column.getConstructMapping().apply(value);
                if (apply instanceof HasSize) ((HasSize) apply).setWidth(column.getMaxWidth());
                add(apply);
                return apply;
            });
            column.getUpdateMapping().accept(value, component);
        }
    }
}
