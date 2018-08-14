package com.acuitybotting.website.dashboard.utils;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;

import java.util.function.Supplier;

/**
 * Created by Zachary Herridge on 8/14/2018.
 */
public class Components {

    public static TextField textField(String placeHolder, Supplier<String> initalValueSupplier){
        TextField textField = new TextField();
        if (placeHolder != null) textField.setPlaceholder(placeHolder);

        if (initalValueSupplier != null){
            String value = initalValueSupplier.get();
            if (value != null) textField.setValue(value);
        }

        return textField;
    }

    public static Button button(String text, ComponentEventListener<ClickEvent<Button>> listener){
        return button(null, text, listener);
    }

    public static Button button(VaadinIcon icon, ComponentEventListener<ClickEvent<Button>> listener){
        return button(icon, null, listener);
    }

    public static Button button(VaadinIcon icon, String text, ComponentEventListener<ClickEvent<Button>> listener){
        Button button = new Button();
        if (icon != null) button.setIcon(icon.create());
        if (text != null) button.setText(text);
        if (listener != null) button.addClickListener(listener);
        return button;
    }
}
