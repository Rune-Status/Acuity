package com.acuitybotting.common.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by Zachary Herridge on 8/23/2018.
 */
public class GsonUtil {

    public static  <T extends JsonElement> String getOrDefault(T object, String defaultValue){
        if (object == null || object.isJsonNull()) return defaultValue;
        return object.getAsString();
    }
}
