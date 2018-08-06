package com.arcvideo.system.util.download;

import java.io.OutputStream;
import java.util.List;

public interface HttpFilesReader {
    void setFileList(List<HttpFileInfo> httpFileInfoList) throws Exception;
    long getTotalFileSize() throws Exception;
    long readBuffer(long pos, long len, OutputStream output) throws Exception;
    long read(HttpFileInfo httpFileInfo, long pos, long len, OutputStream outputStream) throws Exception;
}
