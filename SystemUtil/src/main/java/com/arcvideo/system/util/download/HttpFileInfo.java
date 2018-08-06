package com.arcvideo.system.util.download;

public class HttpFileInfo {
    private long startPos = 0;
    private long endPos = 0;
    private String fileName;

    public HttpFileInfo(long length, String fileName) {
        if (length > 0) {
            endPos = length - 1;
        }
        this.fileName = fileName;
    }

    public HttpFileInfo(long startPos, long endPos, String fileName) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.fileName = fileName;
    }

    public long getLength() {
        if (endPos > 0) {
            return endPos - startPos + 1; // use http mode to get length
        }
        return 0;
    }

    public long getStartPos() {
        return startPos;
    }

    public void setStartPos(long startPos) {
        this.startPos = startPos;
    }

    public long getEndPos() {
        return endPos;
    }

    public void setEndPos(long endPos) {
        this.endPos = endPos;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
