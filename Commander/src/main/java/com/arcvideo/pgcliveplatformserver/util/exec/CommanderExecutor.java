package com.arcvideo.pgcliveplatformserver.util.exec;

import org.apache.commons.exec.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CommanderExecutor {

    private static Logger logger = LogManager.getLogger(CommanderExecutor.class);

    public static int executePipe(File workdir, String cmd, Writer out) throws IOException, InterruptedException {

        int errCode = -1;
        if (cmd != null && cmd.length() > 0) {
            List<String> cmds = parseCmd(cmd);
            ProcessBuilder pb = new ProcessBuilder(cmds);
            if (workdir != null)
                pb.directory(workdir);
            Process proc = pb.start();

            InputStreamReader inr = new InputStreamReader(
                    proc.getInputStream());
            char[] buf = new char[512];
            int len;
            while ((len = inr.read(buf)) != -1) {
                for (int i = 0; i < len; i++) {
                    out.write(buf[i]);
                }
            }
            errCode = proc.waitFor();
        }

        return errCode;
    }

    private static List<String> parseCmd(String cmd) {
        List<String> ret = new ArrayList<String>();
        int p = 0;
        for (int i = 0; i < cmd.length(); i++) {
            char c = cmd.charAt(i);
            if (c == ' ') {
                continue;
            } else if (c == '"') {
                ++i;
                p = cmd.indexOf('"', i);
            } else {
                p = cmd.indexOf(' ', i);
            }
            if (p == -1) {
                p = cmd.length();
            }
            ret.add(cmd.substring(i, p));
            i = p;
        }
        return ret;
    }

    public static int execute(String commandLine) throws IOException {
        return execute(commandLine, false);
    }

    public static int execute(String commandLine, boolean debug) throws IOException {
        CommandLine cmdLine = CommandLine.parse(commandLine);
        if(debug) {
            Log4jOutputStream os = new Log4jOutputStream(logger, Level.DEBUG);
            return executeInternal(cmdLine, null, os, null);
        } else {
            return executeInternal(cmdLine, null, null, null);
        }

    }

    public static int execute(String commandLine, StringWriter writer) throws IOException {
        CommandLine cmdLine = CommandLine.parse(commandLine);
        StringWriterOutputStream os = new StringWriterOutputStream(writer);
        return executeInternal(cmdLine, null, os, null);
    }

    public static int execute(String commandLine, StringWriter writer, long timeout) throws IOException {
        CommandLine cmdLine = CommandLine.parse(commandLine);
        StringWriterOutputStream os = new StringWriterOutputStream(writer);
        return executeInternal(cmdLine, timeout, os, null);
    }

    public static int execute(String commandLine, long timeout) throws IOException {
        return execute(commandLine, timeout, false);
    }

    public static int execute(String commandLine, long timeout, boolean debug) throws IOException {
        CommandLine cmdLine = CommandLine.parse(commandLine);
        if(debug) {
            Log4jOutputStream os = new Log4jOutputStream(logger, Level.DEBUG);
            return executeInternal(cmdLine, timeout, os, null);
        } else {
            return executeInternal(cmdLine, timeout, null, null);
        }
    }

    public static int execute(String[] commandLine) throws IOException {
        return execute(commandLine, false);
    }

    public static int execute(String[] commandLine, StringWriter writer) throws IOException {
        CommandLine cmdLine = new CommandLine(commandLine[0]);
        if(commandLine.length > 1) {
            for(int index = 1; index < commandLine.length; index++) {
                cmdLine.addArgument(commandLine[index], false);
            }
        }
        StringWriterOutputStream os = new StringWriterOutputStream(writer);
        return executeInternal(cmdLine, null, os, null);
    }

    public static int execute(String[] commandLine, boolean debug) throws IOException {
        CommandLine cmdLine = new CommandLine(commandLine[0]);
        if(commandLine.length > 1) {
            for(int index = 1; index < commandLine.length; index++) {
                cmdLine.addArgument(commandLine[index], false);
            }
        }
        if(debug) {
            Log4jOutputStream os = new Log4jOutputStream(logger, Level.DEBUG);
            return executeInternal(cmdLine, null, os, null);
        } else {
            return executeInternal(cmdLine, null, null, null);
        }
    }

    public static int execute(String[] commandLine, File workingDir, long timeout) throws IOException {
        return execute(commandLine, workingDir, false);
    }

    public static int execute(String[] commandLine, File workingDir, boolean debug) throws IOException {
        CommandLine cmdLine = new CommandLine(commandLine[0]);
        if(commandLine.length > 1) {
            for(int index = 1; index < commandLine.length; index++) {
                cmdLine.addArgument(commandLine[index], false);
            }
        }
        if(debug) {
            Log4jOutputStream os = new Log4jOutputStream(logger, Level.DEBUG);
            return executeInternal(cmdLine, null, os, workingDir);
        } else {
            return executeInternal(cmdLine, null, null, workingDir);
        }
    }

    public static int execute(String[] commandLine, long timeout) throws IOException {
        return execute(commandLine, timeout, false);
    }

    public static int execute(String[] commandLine, long timeout, boolean debug) throws IOException {
        CommandLine cmdLine = new CommandLine(commandLine[0]);
        if(commandLine.length > 1) {
            for(int index = 1; index < commandLine.length; index++) {
                cmdLine.addArgument(commandLine[index], false);
            }
        }
        if(debug) {
            Log4jOutputStream os = new Log4jOutputStream(logger, Level.DEBUG);
            return executeInternal(cmdLine, timeout, os, null);
        } else {
            return executeInternal(cmdLine, timeout, null, null);
        }
    }

    public static void asyncExecute(String commandLine, ExecuteWatchdog watchdog, OutputStream os, ExecuteResultHandler resultHandler, File workingDir) throws IOException {
        CommandLine cmdLine = CommandLine.parse(commandLine);
        asyncExecuteInternal(cmdLine, watchdog, os, resultHandler, workingDir);
    }

    public static void asyncExecute(String[] commandLine, ExecuteWatchdog watchdog, OutputStream os, ExecuteResultHandler resultHandler, File workingDir) throws IOException {
        CommandLine cmdLine = new CommandLine(commandLine[0]);
        if(commandLine.length > 1) {
            for(int index = 1; index < commandLine.length; index++) {
                cmdLine.addArgument(commandLine[index], false);
            }
        }
        asyncExecuteInternal(cmdLine, watchdog, os, resultHandler, workingDir);
    }

    private static int executeInternal(CommandLine cmdLine, Long timeout, OutputStream os, File workingDir) throws IOException {
        DefaultExecutor executor = new DefaultExecutor();
        if(timeout != null && timeout != 0) {
            ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
            executor.setWatchdog(watchdog);
        }
        if(os != null) {
            PumpStreamHandler ps = new PumpStreamHandler(os);
            executor.setStreamHandler(ps);
        }
        if(workingDir != null) {
            executor.setWorkingDirectory(workingDir);
        }
        int exitValue = executor.execute(cmdLine);
        return exitValue;
    }

    private static void asyncExecuteInternal(CommandLine cmdLine, ExecuteWatchdog watchdog, OutputStream os, ExecuteResultHandler resultHandler, File workingDir) throws IOException {
        DefaultExecutor executor = new DefaultExecutor();
        if(watchdog != null ) {
            executor.setWatchdog(watchdog);
        }
        if(os != null) {
            PumpStreamHandler ps = new PumpStreamHandler(os);
            executor.setStreamHandler(ps);
        }
        if(resultHandler == null) {
            resultHandler = new DefaultExecuteResultHandler();
        }
        if(workingDir != null) {
            executor.setWorkingDirectory(workingDir);
        }
        executor.execute(cmdLine, resultHandler);
    }
}
