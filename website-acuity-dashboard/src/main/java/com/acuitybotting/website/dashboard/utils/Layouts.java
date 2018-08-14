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

    public static HorizontalLayout wrapHorizontal(FlexComponent.Alignment alignment, String width, Component... components){
        HorizontalLayout horizontalLayout = new HorizontalLayout(components);
        if (alignment != null) horizontalLayout.setAlignItems(alignment);
        if (width != null) horizontalLayout.setWidth(width);
        horizontalLayout.setPadding(false);
        horizontalLayout.setMargin(false);
        return horizontalLayout;
    }

}
