package com.acuitybotting.website.dashboard.components.general.list_display;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.function.Consumer;

public class InteractiveList<T> extends VerticalLayout {

    private Consumer<InteractiveList> loadFunction;

    private HorizontalLayout controlBar = new HorizontalLayout();
    private HorizontalLayout controls = new HorizontalLayout();
    private TextField searchField = new TextField();

    private HorizontalLayout headers = new HorizontalLayout();

    private VerticalLayout list = new VerticalLayout();

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

    public InteractiveList withRow(InteractiveListRow row){
        list.add(row);
        return this;
    }

    public InteractiveList withLoadFuncation(Consumer<InteractiveList> consumer){
        this.loadFunction = consumer;
        return this;
    }

    public InteractiveList load(){
        loadFunction.accept(this);
        return this;
    }

    public HorizontalLayout getHeaders() {
        return headers;
    }

    public HorizontalLayout getControls() {
        return controls;
    }
}
