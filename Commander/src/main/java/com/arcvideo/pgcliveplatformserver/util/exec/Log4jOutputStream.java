package com.arcvideo.pgcliveplatformserver.util.exec;

import org.apache.commons.exec.LogOutputStream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class Log4jOutputStream extends LogOutputStream{

    private Logger logger;
    private Level level;

    public Log4jOutputStream(Logger logger, Level level) {
        this.logger = logger;
        this.level = level;
    }

    @Override
    protected void processLine(String s, int i) {
        logger.log(level, s);
    }
}
