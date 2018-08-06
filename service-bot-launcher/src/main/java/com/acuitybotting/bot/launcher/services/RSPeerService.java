package com.acuitybotting.bot.launcher.services;

import com.acuitybotting.common.utils.OS;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Zachary Herridge on 8/6/2018.
 */
@Service
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

    public String getSession() throws IOException {
        if (!Files.exists(Paths.get(ME))) {
            return null;
        }
        return new String(Files.readAllBytes(Paths.get(ME)));
    }
}
