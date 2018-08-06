package com.arcvideo.pgcliveplatformserver.util.exec;

import org.apache.commons.exec.LogOutputStream;

import java.io.StringWriter;

public class StringWriterOutputStream extends LogOutputStream{
    private StringWriter writer;

    public StringWriterOutputStream(StringWriter writer) {
        this.writer = writer;
    }

    @Override
    protected void processLine(String s, int i) {
        this.writer.write(s);
        this.writer.write("\n");
    }
}
