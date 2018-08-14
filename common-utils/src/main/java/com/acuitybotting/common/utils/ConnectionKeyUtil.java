package com.acuitybotting.common.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class ConnectionKeyUtil {

    public static void writeKey(String key){
        try {
            Files.write(new File(System.getProperty("user.home"), "acuity-connection-key.txt").toPath(), key.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JsonObject decode(String key){
        return new Gson().fromJson(new String(Base64.getDecoder().decode(key)), JsonObject.class);
    }

    public static String findKey() {
        String key = System.getenv("acuity.connection-key");
        if (key != null) return key;
        key = System.getProperty("acuity.connection-key");
        if (key != null) return key;
        key = findKey(new File(System.getProperty("user.dir")));
        if (key != null) return key;
        return findKey(new File(System.getProperty("user.home")));
    }

    private static String findKey(File base){
        File[] files = base.listFiles();
        if (files == null) return null;
        for (File file : files) {
            if (file.getName().contains("acuity-connection-key")) {
                try {
                    return new String(Files.readAllBytes(file.toPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
