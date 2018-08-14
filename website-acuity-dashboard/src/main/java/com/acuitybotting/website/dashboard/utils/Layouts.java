package com.acuitybotting.website.dashboard.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

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

}
