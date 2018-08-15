package com.acuitybotting.website.dashboard.components.general.list_display;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
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
import java.util.stream.Stream;

@Getter
public class InteractiveList<T> extends VerticalLayout {

    private Function<T, String> idMapper;
    private Supplier<Collection<T>> loadSupplier;
    private Runnable loadAction;

    private HorizontalLayout controlBar = new HorizontalLayout();
    private HorizontalLayout controls = new HorizontalLayout();
    private TextField searchField = new TextField();
    private Button refreshButton = new Button(VaadinIcon.REFRESH.create());
    private Checkbox selectAll = new Checkbox();

    private Button nextPage = new Button(VaadinIcon.ARROW_RIGHT.create());
    private Span pagePosition = new Span();
    private Button previousPage = new Button(VaadinIcon.ARROW_LEFT.create());

    private Div selectionCount = new Div();

    private Div headers = new Div();

    private int currentPage = 1;
    private int entriesPerPage = 10;

    private VerticalLayout list = new VerticalLayout();
    private HorizontalLayout footers = new HorizontalLayout();
    private List<InteractiveListColumn> columns = new ArrayList<>();
    private TreeMap<String, InteractiveListRow<T>> rows = new TreeMap<>();
    private Collection<T> values = Collections.emptyList();
    private String searchText = "";
    private Function<T, String> searchableFunction;

    public InteractiveList() {
        setMargin(false);
        setPadding(false);

        refreshButton.addClickListener(event -> load());
        searchField.addValueChangeListener(event -> applySearch(searchField.getValue()));
        selectAll.addValueChangeListener(event -> {
            for (InteractiveListRow<T> row : rows.values()) {
                if (row.isVisible() && row.isEnabled()) row.getSelectionBox().setValue(event.getValue());
            }
            updateSelectionCount();
        });

        controlBar.setWidth("100%");
        controlBar.setPadding(false);
        controlBar.setMargin(false);

        searchField.setVisible(false);
        searchField.setPlaceholder("Search...");

        controls.setWidth("100%");
        controls.setPadding(false);
        controls.setMargin(false);

        refreshButton.setWidth("45px");
        controlBar.add(controls, refreshButton, searchField);

        headers.setWidth("100%");
        headers.getStyle().set("display", "-webkit-inline-box");
        headers.getClassNames().add("acuity-interactive-list-headers");

        headers.add(selectAll);

        list.setPadding(false);
        list.setMargin(false);
        list.getStyle().set("overflow", "auto");

        footers.setWidth("100%");
        footers.setPadding(false);
        footers.setMargin(false);

        previousPage.addClickListener(buttonClickEvent -> {
            currentPage--;
            updatePage();
        });

        nextPage.addClickListener(buttonClickEvent -> {
            currentPage++;
            updatePage();
        });


        pagePosition.setHeight("100%");

        footers.add(previousPage, pagePosition, nextPage);

        add(controlBar, headers, list, footers);
    }

    public void updateSelectionCount() {
        long count = rows.values().stream().filter(row -> row.getSelectionBox().getValue()).count();
        if (count == 0) selectionCount.setVisible(false);
        else {
            selectionCount.setText("(" + String.valueOf(count) + "/" + rows.size() + ") selected");
            if (!selectionCount.isVisible()) selectionCount.setVisible(true);
        }
    }

    public void applySearch(String searchTxt) {
        this.searchText = searchTxt;
        updatePage();
    }

    public Stream<InteractiveListRow<T>> getSelectedRows() {
        return rows.values().stream().filter(row -> row.getSelectionBox().getValue());
    }

    public Stream<T> getSelectedValues() {
        return getSelectedRows().map(InteractiveListRow::getValue);
    }

    private void updatePage() {
        Set<T> filtered = values.stream().filter(t -> searchText.isEmpty() || searchableFunction == null || searchableFunction.apply(t).toLowerCase().contains(searchText.toLowerCase())).collect(Collectors.toSet());
        int pageCount = (int) Math.ceil((double) filtered.size() / (double) entriesPerPage);
        currentPage = Math.max(Math.min(currentPage, pageCount), 1);

        pagePosition.setText((pageCount == 0 ? 0 : currentPage) + "/" + pageCount);

        Map<String, Set<T>> grouped =
                filtered.stream()
                        .sorted(Comparator.comparing(o -> idMapper.apply(o)))
                        .skip((currentPage - 1) * entriesPerPage).limit(entriesPerPage)
                        .collect(Collectors.groupingBy(idMapper, Collectors.toSet()));

        for (Map.Entry<String, InteractiveListRow<T>> entry : new HashSet<>(rows.entrySet())) {
            if (!grouped.containsKey(entry.getKey())) entry.getValue().removeFromList();
        }

        for (Map.Entry<String, Set<T>> entry : grouped.entrySet()) {
            addOrUpdate(entry.getKey(), entry.getValue().stream().findAny().orElse(null));
        }
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
        list.expand(row);

        return this;
    }

    public InteractiveList<T> withLoad(Function<T, String> idMapper, Supplier<Collection<T>> loadSupplier) {
        return withLoad(idMapper, loadSupplier, null);
    }

    public InteractiveList<T> withLoadAction(Function<T, String> idMapper, Runnable action) {
        return withLoad(idMapper, null, action);
    }

    public InteractiveList<T> withLoad(Function<T, String> idMapper, Supplier<Collection<T>> loadSupplier, Runnable action) {
        this.idMapper = idMapper;
        this.loadSupplier = loadSupplier;
        this.loadAction = action;
        return this;
    }

    public InteractiveList<T> withSearchable(Function<T, String> searchableFunction) {
        this.searchableFunction = searchableFunction;
        searchField.setVisible(true);
        return this;
    }

    public InteractiveList<T> update(Collection<T> values) {
        this.values = values;
        updatePage();
        return this;
    }

    public InteractiveList<T> load() {
        getUI().ifPresent(ui -> ui.access(() -> {
            if (loadAction != null) loadAction.run();
            if (loadSupplier != null) update(loadSupplier.get());
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

    public InteractiveList hideControls() {
        controlBar.setVisible(false);
        return this;
    }
}
