package com.arcvideo.pgcliveplatformserver.util.exec;

import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;

public class KillableExecuteWatchdog extends ExecuteWatchdog {
    private static Logger logger = LogManager.getLogger(KillableExecuteWatchdog.class);
    private long mPid;

    public KillableExecuteWatchdog(long timeout) {
        super(timeout);
        mPid = 0;
    }

    @Override
    public synchronized void start(Process processToMonitor) {
        mPid = getPid(processToMonitor);
        logger.debug("fpclient pid: " + mPid);
        super.start(processToMonitor);
    }

    @Override
    public synchronized void stop() {
        super.stop();
        mPid = 0;
    }

    public synchronized void terminateProcess() {
        if(mPid != 0) {
            try {
                Runtime.getRuntime().exec("kill -SIGINT " + mPid);
            } catch (IOException e) {
                //ignored
            }
        }
    }

    public synchronized boolean running() {
        if(mPid != 0) {
            try {
                Process p = Runtime.getRuntime().exec("kill -0 " + mPid);
                p.waitFor();
                int r = p.exitValue();
                return r == 0;
            } catch (IOException e) {
                return false;
            } catch (InterruptedException e) {
                return false;
            }
        }
        return false;
    }

    private long getPid(Process p) {
        long pid = -1;

        try {
            if (p.getClass().getName().equals("java.lang.UNIXProcess")) {
                Field f = p.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                pid = f.getLong(p);
                f.setAccessible(false);
            }
        } catch (Exception e) {
            pid = -1;
        }
        return pid;
    }
}
