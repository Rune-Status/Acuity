package com.acuitybotting.common.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by Zachary Herridge on 8/23/2018.
 */
public class GsonUtil {

    private static final Gson gson = new Gson();

    public static <T extends JsonElement> String getOrDefault(T object, String defaultValue){
        if (object == null || object.isJsonNull()) return defaultValue;
        String asString = object.getAsString();
        if (asString.isEmpty()) return defaultValue;
        return asString;
    }

    public static Gson getGson() {
        return gson;
    }
}
