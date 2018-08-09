package com.acuitybotting.website.dashboard.components.general.list_display;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
public class InteractiveList<T> extends VerticalLayout {

    private Function<T, String> idMapper;
    private Supplier<Collection<T>> loadSupplier;

    private HorizontalLayout controlBar = new HorizontalLayout();
    private HorizontalLayout controls = new HorizontalLayout();
    private TextField searchField = new TextField();
    private Button refreshButton = new Button(VaadinIcon.REFRESH.create());

    private HorizontalLayout headers = new HorizontalLayout();

    private VerticalLayout list = new VerticalLayout();

    private List<InteractiveListColumn> columns = new ArrayList<>();
    private Map<String, InteractiveListRow<T>> rows = new HashMap<>();

    public InteractiveList() {
        setMargin(false);
        setPadding(false);

        refreshButton.addClickListener(buttonClickEvent -> load());

        searchField.addValueChangeListener(textFieldStringComponentValueChangeEvent -> applySearch(searchField.getValue()));

        controlBar.setWidth("100%");
        controlBar.setPadding(false);
        controlBar.setMargin(false);

        searchField.setPlaceholder("Search...");

        controls.setWidth("100%");
        controls.setPadding(false);
        controls.setMargin(false);

        controlBar.add(controls, refreshButton, searchField);

        headers.setWidth("100%");
        headers.setPadding(false);
        headers.setMargin(false);
        headers.getClassNames().add("acuity-interactive-list-headers");

        headers.add(new Checkbox());

        list.setPadding(false);
        list.setMargin(false);

        add(controlBar, headers, list);
    }

    public void applySearch(String searchTxt) {
        if (searchTxt != null){
            searchTxt = searchTxt.toLowerCase();
            if (searchTxt.isEmpty()) searchTxt = null;
        }

        for (InteractiveListRow<T> row : rows.values()) {
            String finalSearchTxt = searchTxt;
            boolean visible = searchTxt == null || row.getChildren().anyMatch(component -> {
                if (component instanceof HasText) {
                    String text = ((HasText) component).getText();
                    return text != null && text.toLowerCase().contains(finalSearchTxt);
                }
                return false;
            });
            row.setVisible(visible);
        }
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

        row = new InteractiveListRow<>(this, id);
        rows.put(id, row);
        row.update(value);
        list.add(row);

        return this;
    }

    public InteractiveList<T> withLoad(Function<T, String> idMapper, Supplier<Collection<T>> loadSupplier) {
        this.idMapper = idMapper;
        this.loadSupplier = loadSupplier;
        return this;
    }

    public InteractiveList<T> load() {
        getUI().ifPresent(ui -> ui.access(() -> {
            Map<String, Set<T>> load = loadSupplier.get().stream().collect(Collectors.groupingBy(idMapper, Collectors.toSet()));

            for (Map.Entry<String, InteractiveListRow<T>> entry : rows.entrySet()) {
                if (!load.containsKey(entry.getKey())) entry.getValue().removeFromList();
            }

            for (Map.Entry<String, Set<T>> entry : load.entrySet()) {
                addOrUpdate(entry.getKey(), entry.getValue().stream().findAny().orElse(null));
            }
        }));

        return this;
    }

    public <R extends Component> InteractiveListColumn<T, R> withColumn(String header, String maxWidth, Function<T, R> constructMapping, BiConsumer<T, R> updateMapping) {
        InteractiveListColumn<T, R> column = new InteractiveListColumn<>(header, maxWidth, constructMapping, updateMapping);
        columns.add(column);
        headers.add(column.getHeaderComponent());
        return column;
    }

    public void removeRow(String id, InteractiveListRow<T> row) {
        rows.remove(id);
        list.remove(row);
    }
}
