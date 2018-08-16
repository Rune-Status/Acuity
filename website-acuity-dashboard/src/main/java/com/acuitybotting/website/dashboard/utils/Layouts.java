package com.acuitybotting.website.dashboard.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Layouts {

    public static HorizontalLayout wrapHorizontal(Component... components){
        return wrapHorizontal(null, null, components);
    }

    public static HorizontalLayout wrapHorizontal(String width, Component... components){
        return wrapHorizontal(null, width, components);
    }

    public static HorizontalLayout wrapHorizontal(FlexComponent.JustifyContentMode justifyContentMode, String width, Component... components){
        HorizontalLayout horizontalLayout = new HorizontalLayout(components);
        if (justifyContentMode != null) horizontalLayout.setJustifyContentMode(justifyContentMode);
        if (width != null) horizontalLayout.setWidth(width);
        horizontalLayout.setPadding(false);
        horizontalLayout.setMargin(false);
        return horizontalLayout;
    }

    public static VerticalLayout wrapVertical(String width, Component... components) {
        return wrapVertical(null, width, components);
    }

    public static VerticalLayout wrapVertical(FlexComponent.JustifyContentMode justifyContentMode, String width, Component... components) {
        VerticalLayout verticalLayout = new VerticalLayout(components);
        if (justifyContentMode != null) verticalLayout.setJustifyContentMode(justifyContentMode);
        if (width != null) verticalLayout.setWidth(width);
        verticalLayout.setPadding(false);
        verticalLayout.setMargin(false);
        return verticalLayout;
    }
}
