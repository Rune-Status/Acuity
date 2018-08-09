package com.acuitybotting.website.dashboard.components.general.list_display;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class InteractiveList<T> extends VerticalLayout {

    private static final ExecutorService loadPool = ExecutorUtil.newExecutorPool(5);

    private Consumer<InteractiveList> loadFunction;

    private HorizontalLayout controlBar = new HorizontalLayout();
    private HorizontalLayout controls = new HorizontalLayout();
    private TextField searchField = new TextField();

    private HorizontalLayout headers = new HorizontalLayout();

    private VerticalLayout list = new VerticalLayout();

    private List<InteractiveListColumn> columns = new ArrayList<>();
    private Map<String, InteractiveListRow<T>> rows = new HashMap<>();

    public InteractiveList() {
        setMargin(false);
        setPadding(false);

        controlBar.setWidth("100%");
        controlBar.setPadding(false);
        controlBar.setMargin(false);

        searchField.setPlaceholder("Search...");

        controls.setWidth("100%");
        controls.setPadding(false);
        controls.setMargin(false);

        controlBar.add(controls, searchField);

        headers.setWidth("100%");
        headers.setPadding(false);
        headers.setMargin(false);
        headers.getClassNames().add("acuity-interactive-list-headers");

        headers.add(new Checkbox());

        list.setPadding(false);
        list.setMargin(false);

        add(controlBar, headers, list);
    }

    public InteractiveList<T> withRow(InteractiveListRow row) {
        list.add(row);
        return this;
    }

    public InteractiveList<T> addOrUpdate(String id, T value) {
        InteractiveListRow<T> row = rows.get(id);

        if (row != null) {
            row.update(value);
            return this;
        }

        row = new InteractiveListRow<>(this);
        rows.put(id, row);
        row.update(value);
        list.add(row);

        return this;
    }

    public InteractiveList<T> withLoad(Consumer<InteractiveList> consumer) {
        this.loadFunction = consumer;
        return this;
    }

    public InteractiveList<T> load() {
        getUI().ifPresent(ui -> {
            loadPool.submit(() -> {
                ui.access(() -> {
                    loadFunction.accept(this);
                });
            });
        });

        return this;
    }

    public <R extends Component> InteractiveListColumn<T, R> withColumn(String header, String maxWidth, Function<T, R> constructMapping, BiConsumer<T, R> updateMapping) {
        InteractiveListColumn<T, R> column = new InteractiveListColumn<>(header, maxWidth, constructMapping, updateMapping);
        columns.add(column);
        headers.add(column.getHeaderComponent());
        return column;
    }
}
