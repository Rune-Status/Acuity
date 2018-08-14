package com.acuitybotting.website.dashboard.utils;

import com.vaadin.flow.component.notification.Notification;

public class Notifications {

    public static Notification display(String message, Object... values){
        for (Object value : values) {
            message = message.replaceFirst("\\{}", String.valueOf(value));
        }
        return Notification.show(message, 3000, Notification.Position.TOP_END);
    }

    public static Notification error(String message, Object... values) {
        return display(message, values);
    }
}
