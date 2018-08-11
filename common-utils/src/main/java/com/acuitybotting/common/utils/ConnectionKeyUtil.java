package com.acuitybotting.common.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ConnectionKeyUtil {

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
