package com.acuitybotting.bot.launcher.utils;

import com.acuitybotting.bot.launcher.enviroments.RSPeerEnviroment;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

public class CommandLine {

    public static Process runCommand(String command) throws IOException {
        return System.getProperty("os.name").contains("Windows") ? Runtime.getRuntime().exec("cmd.exe /c start /min " + command) : Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
    }

    public static String replacePlaceHolders(String command){
        command = command.replaceAll("\\{RSPEER_SYSTEM_HOME}", Matcher.quoteReplacement(RSPeerEnviroment.getSystemHome()));
        command = command.replaceAll("\\{RSPEER_JAVA_PATH}", Matcher.quoteReplacement(RSPeerEnviroment.getJavaPath()));

        command = command.replaceAll("\\{USER_HOME}", Matcher.quoteReplacement(System.getProperty("user.home")));
        command = command.replaceAll("\\{USER_DIR}", Matcher.quoteReplacement(System.getProperty("user.dir")));
        command = command.replaceAll("\\{USER_NAME}", Matcher.quoteReplacement(System.getProperty("user.name")));
        command = command.replaceAll("\\{JAVA_HOME}", Matcher.quoteReplacement(System.getProperty("java.home")));

        command = command.replaceAll("/", Matcher.quoteReplacement(File.separator));
        command = command.replaceAll("\\\\", Matcher.quoteReplacement(File.separator));
        return command;
    }
}
