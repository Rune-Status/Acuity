package com.acuitybotting.website.dashboard.components.general.list_display;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Getter
@Slf4j
public class InteractiveListRow<T> extends HorizontalLayout {

    private Checkbox selectionBox = new Checkbox();
    private InteractiveList<T> list;
    private String rowId;
    private Map<String, Object> columnComponents = new HashMap<>();
    private T value;

    public InteractiveListRow(InteractiveList parent, String id) {
        this.list = parent;
        this.rowId = id;

        setPadding(false);
        setWidth("100%");

        getClassNames().add("acuity-interactive-list-row");
        selectionBox.addValueChangeListener(event -> list.updateSelectionCount());
        add(selectionBox);
    }

    @SuppressWarnings("unchecked")
    public void update(T value) {
        this.value = value;
        for (InteractiveListColumn column : list.getColumns()) {
            try {
                Object component = columnComponents.computeIfAbsent(column.getUid(), s -> {
                    Component apply = (Component) column.getConstructMapping().apply(value);
                    if (apply instanceof HasSize) ((HasSize) apply).setWidth(column.getMaxWidth());
                    add(apply);
                    return apply;
                });
                column.getUpdateMapping().accept(value, component);
            } catch (Throwable e) {
                log.info("Error while updating row.");
            }
        }
    }

    public void removeFromList() {
        list.removeRow(rowId, this);
    }
}
