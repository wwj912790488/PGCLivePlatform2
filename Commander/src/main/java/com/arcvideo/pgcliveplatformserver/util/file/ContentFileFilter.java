package com.arcvideo.pgcliveplatformserver.util.file;

public interface ContentFileFilter {
    boolean accept(String path);
    boolean acceptFile(String path);
    boolean acceptFolder(String path);
}
