package com.acuitybotting.website.dashboard.components.general.list_display;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class InteractiveListRow<T> extends HorizontalLayout {

    private Checkbox selectionBox = new Checkbox();
    private InteractiveList<T> list;
    private String rowId;
    private Map<String, Object> columnComponents = new HashMap<>();
    private T value;

    public InteractiveListRow(InteractiveList parent, String id) {
        this.list = parent;
        this.rowId = id;
        setMargin(false);
        setWidth("100%");
        getClassNames().add("acuity-interactive-list-row");
        selectionBox.addValueChangeListener(event -> list.updateSelectionCount());
        add(selectionBox);
    }

    public String getSearchableText(){
        return getChildren()
                .filter(component -> component instanceof HasText)
                .map(component -> ((HasText) component).getText())
                .filter(Objects::nonNull)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" "));
    }

    @SuppressWarnings("unchecked")
    public void update(T value) {
        this.value = value;
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

    public void removeFromList() {
        list.removeRow(rowId, this);
    }
}
