package com.acuitybotting.bot.launcher.enviroments;

import com.acuitybotting.common.utils.OS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Zachary Herridge on 8/6/2018.
 */
public class RSPeerEnviroment {
    public static final String CACHE = getSystemHome() + File.separator + "RSPeer" + File.separator + "cache" + File.separator;

    public static String getSystemHome() {
        return OS.get() == OS.WINDOWS ? System.getProperty("user.home") + "/Documents/" : System.getProperty("user.home") + "/";
    }

    public static String getJavaPath() {
        OS os = OS.get();
        if (os == OS.WINDOWS) {
            Path path = Paths.get(CACHE, "bot-assets", "jre", "bin", "java.exe");
            if (path.toFile().exists())
                return path.toString();
            return Paths.get(CACHE, "bot-assets", "jre-windows", "bin", "java.exe").toString();
        }
        Path path = Paths.get(CACHE, "bot-assets", "jre", "bin", "java");
        if (path.toFile().exists())
            return path.toString();
        if (os == OS.MAC) {
            return Paths.get(CACHE, "bot-assets", "jre-mac", "bin", "java").toString();
        }
        if (os == OS.LINUX || os == OS.UNKNOWN) {
            return Paths.get(CACHE, "bot-assets", "jre-linux", "bin", "java").toString();

        }
        return Paths.get(CACHE, "bot-assets", "jre", "bin", "java").toString();
    }

    public static String getSession() throws IOException {
        String ME = CACHE + "rspeer_me";
        if (!Files.exists(Paths.get(ME))) return null;
        return new String(Files.readAllBytes(Paths.get(ME)));
    }
}
