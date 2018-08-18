package com.acuitybotting.data.flow.messaging.services.identity;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
public class RabbitUtil {

    public static String routeToUserId(String route){
        int start = route.indexOf("user.") + "user.".length();
        return route.substring(start, route.indexOf(".", start));
    }

    public static String connectionNameToType(String connectionName){
        if (connectionName == null || connectionName.trim().isEmpty()) return null;
        String[] split = connectionName.split("_");
        if (split.length < 2) return null;
        return split[0];
    }
}
