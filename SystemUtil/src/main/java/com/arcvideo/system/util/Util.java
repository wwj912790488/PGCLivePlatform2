package com.arcvideo.system.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
    private static final Logger LOG = LoggerFactory.getLogger(Util.class);

    private Util() {
    }

    public static void sleep(long ms) {
        try {
            LOG.trace("Sleeping for {} ms", ms);
            Thread.sleep(ms);
        } catch (InterruptedException e) { // NOSONAR squid:S2142
            LOG.trace("", e);
            LOG.warn("Interrupted while sleeping for {} ms", ms);
        }
    }

    public static void sleepAfter(long startTime, long ms) {
        long now = System.currentTimeMillis();
        long until = startTime + ms;
        LOG.trace("Sleeping until {}", until);
        if (now < until) {
            sleep(until - now);
        }
    }
}