package com.acuitybotting.common.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Base64;

/**
 * Created by Zachary Herridge on 7/10/2018.
 */
public class JwtUtil {

    private static Gson gson = new Gson();

    public static JsonObject decodeBody(String jwt){
        String[] split = jwt.split("\\.");
        String body = new String(Base64.getDecoder().decode(split[1]));
        return gson.fromJson (body, JsonObject.class);
    }

    public static JsonObject decodeHeaders(String jwt){
        String[] split = jwt.split("\\.");
        String body = new String(Base64.getDecoder().decode(split[0]));
        return gson.fromJson (body, JsonObject.class);
    }
}
