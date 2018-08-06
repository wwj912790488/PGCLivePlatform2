package com.arcvideo.system.util;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExecutingCommand {
    private static final Logger logger = LoggerFactory.getLogger(ExecutingCommand.class);

    private static final long timeout = 30000;

    private ExecutingCommand() {
    }

    private static List<String> __runNative(String[] cmdToRunWithArgs) throws IOException {
        List sa = new ArrayList<>();
        CommandLine cmdLine = new CommandLine(cmdToRunWithArgs[0]);
        for (int i = 1; i < cmdToRunWithArgs.length; ++i) {
            cmdLine.addArgument(cmdToRunWithArgs[i], false);
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(32768);

        DefaultExecutor executor = new DefaultExecutor();
        ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
        executor.setWatchdog(watchdog);
        PumpStreamHandler ps = new PumpStreamHandler(byteArrayOutputStream);
        executor.setStreamHandler(ps);
        logger.info("{}", cmdLine);
        int exitValue = executor.execute(cmdLine);
        String outputString = byteArrayOutputStream.toString("UTF-8");
        if (StringUtils.isNotEmpty(outputString)) {
            sa = Arrays.asList(outputString.split("[\r\n]+"));
        }
        return sa;
    }

    public static List<String> runNative(String cmdToRun) {
        String[] cmd = cmdToRun.split(" ");
        return runNative(cmd);
    }

    public static List<String> runNative(String[] cmdToRunWithArgs) {
        List sa = new ArrayList<>();
        try {
            sa = __runNative(cmdToRunWithArgs);
        }
        catch (IOException e) {
            logger.debug("{}", e);
            logger.error("failed to run command");
        }
        return sa;
    }

    public static List<String> runNativeWithException(String[] cmdToRunWithArgs) throws IOException {
        List sa = new ArrayList<>();
        try {
            sa = __runNative(cmdToRunWithArgs);
        }
        catch (IOException e) {
            logger.debug("{}", e);
            logger.error("failed to run command");
            throw e;
        }
        return sa;
    }

    public static List<String> runShellNative(String cmdToRun) {
        String[] commandLine = new String[]{"/bin/sh", "-c", cmdToRun};
        return runNative(commandLine);
    }

    public static List<String> runShellNativeWithException(String cmdToRun) throws IOException {
        String[] commandLine = new String[]{"/bin/sh", "-c", cmdToRun};
        return runNativeWithException(commandLine);
    }

    public static String getShellFirstAnswer(String cmd2launch) {
        return getShellAnswerAt(cmd2launch, 0);
    }

    public static String getShellFirstAnswerWithException(String cmd2launch) throws IOException, InterruptedException {
        return getShellAnswerAtWithException(cmd2launch, 0);
    }

    public static String getShellAnswerAt(String cmd2launch, int answerIdx) {
        List<String> sa = ExecutingCommand.runShellNative(cmd2launch);

        if (answerIdx >= 0 && answerIdx < sa.size()) {
            return sa.get(answerIdx);
        }
        return "";
    }

    public static String getShellAnswerAtWithException(String cmd2launch, int answerIdx) throws IOException {
        List<String> sa = ExecutingCommand.runShellNativeWithException(cmd2launch);

        if (answerIdx >= 0 && answerIdx < sa.size()) {
            return sa.get(answerIdx);
        }
        return "";
    }
}
