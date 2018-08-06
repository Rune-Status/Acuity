package com.acuitybotting.bot.launcher.services;

import com.acuitybotting.bot.launcher.utils.CommandLine;
import com.acuitybotting.common.utils.OS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Zachary Herridge on 8/6/2018.
 */
@Service
@Slf4j
public class RSPeerService {

    public static final String APPLICATION_NAME = "RSPeer";
    public static final String HOME = getSystemHome() + File.separator + APPLICATION_NAME + File.separator;
    public static final String CACHE = HOME + "cache" + File.separator;
    public static final String ME = CACHE + "rspeer_me";
    public static final String MAIN_JAR = CACHE + "rspeer.jar";
    public static String API_URL = "https://api.rspeer.org/v2";
    public static String SSO_URL = "https://sso.rspeer.org/?redirect=https://rspeer.org";
    public static String DOWNLOAD_URL = "https://download.rspeer.org/";

    private static String getSystemHome() {
        return OS.get() == OS.WINDOWS ? System.getProperty("user.home") + "/Documents/"
                : System.getProperty("user.home") + "/";
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

    public void launch(String connectionId) {
        if (OS.get() != OS.WINDOWS) {
            try {
                CommandLine.runCommand("chmod +x " + getJavaPath());
            } catch (CommandLine.CommandLineException | IOException e) {
                log.error("Error during launch.", e);
            }
        }

        try {
            CommandLine.runJar(getJavaPath(), Paths.get(MAIN_JAR), new ArrayList<>(), Collections.singletonList("-acuityConnectionId " + connectionId));
        } catch (Exception e) {
            log.error("Error during launch.", e);
        }
    }

    public String getSession() throws IOException {
        if (!Files.exists(Paths.get(ME))) {
            return null;
        }
        return new String(Files.readAllBytes(Paths.get(ME)));
    }
}
