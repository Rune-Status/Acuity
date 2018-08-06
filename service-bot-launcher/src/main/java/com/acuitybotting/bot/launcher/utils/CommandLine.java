package com.acuitybotting.bot.launcher.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class CommandLine {

    private static String system = System.getProperty("os.name");

    public static void runJar(String javaPath, Path path, List<String> vmArgs, List<String> appArgs) throws CommandLineException, IOException {
        String command = buildCommand(javaPath, path, vmArgs, appArgs);
        runCommand(command);
    }

    public static String buildCommand(String javaPath, Path path, List<String> vmArgs, List<String> appArgs) {
        StringBuilder builder = new StringBuilder();
        builder.append(javaPath).append(" ");
        builder.append("-Djava.net.preferIPv4Stack=true").append(" ");
        for (String vmArg : vmArgs) {
            builder.append(vmArg).append(" ");
        }
        builder.append("-jar").append(" ").append("\"").append(path.toString()).append("\"").append(" ");
        for (String appArg : appArgs) {
            builder.append(appArg).append(" ");
        }
        String command = builder.toString();
        int indexAgent = command.indexOf("-javaagent");
        if (indexAgent != -1) {
            command = command.replace("-javaagent", "");
            String sub = command.substring(indexAgent, command.lastIndexOf("-Dfile"));
            command = command.replace(sub, "");
        }
        return command;
    }

    public static Process runCommand(String command) throws CommandLineException, IOException {
        return system.contains("Windows") ? Runtime.getRuntime().exec("cmd.exe /c start " + command) : Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
    }

    public static class CommandLineException extends Exception {

        public CommandLineException(String message) {
            super(message);
        }

        @Override
        public String getMessage() {
            return super.getMessage();
        }
    }
}
