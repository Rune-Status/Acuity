package com.acuitybotting.common.utils.connection_configuration;

import com.acuitybotting.common.utils.connection_configuration.domain.ConnectionConfiguration;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Optional;

public class ConnectionConfigurationUtil {

    public static void write(ConnectionConfiguration encodedConfiguration){
        try {
            Files.write(new File(System.getProperty("user.dir"), "acuity-connection.txt").toPath(), Base64.getEncoder().encode(new Gson().toJson(encodedConfiguration).getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Optional<ConnectionConfiguration> decode(String encodedConfiguration){
        if (encodedConfiguration == null) return Optional.empty();
        return Optional.ofNullable(new Gson().fromJson(new String(Base64.getDecoder().decode(encodedConfiguration)), ConnectionConfiguration.class));
    }

    public static JsonObject decodeConnectionKey(String connectionKey){
        if (connectionKey == null) return null;
        return new Gson().fromJson(new String(Base64.getDecoder().decode(connectionKey)), JsonObject.class);
    }

    public static String find() {
        String key = System.getenv("acuity.connection");
        if (key != null) return key;
        key = System.getProperty("acuity.connection");
        if (key != null) return key;
        key = findInFile(new File(System.getProperty("user.dir")));
        if (key != null) return key;
        return findInFile(new File(System.getProperty("user.home")));
    }

    private static String findInFile(File base){
        File[] files = base.listFiles();
        if (files == null) return null;
        for (File file : files) {
            if (file.getName().contains("acuity-connection")) {
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
