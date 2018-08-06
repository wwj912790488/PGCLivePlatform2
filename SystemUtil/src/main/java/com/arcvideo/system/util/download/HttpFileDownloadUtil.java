package com.arcvideo.system.util.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HttpFileDownloadUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpFileDownloadUtil.class);

    private HttpFilesReader httpFilesReader = null;

    private void createHttpFileReader(boolean localFile) {
        if (localFile) {
            httpFilesReader = new HttpLocalFilesReader();
        }
        else {
            httpFilesReader = new HttpUrlFilesReader();
        }
    }

    public void openFile(String fileName, boolean localFile) throws Exception {
        List<HttpFileInfo> httpFileInfoList = new ArrayList();
        httpFileInfoList.add(new HttpFileInfo(0, fileName));
        createHttpFileReader(localFile);
        httpFilesReader.setFileList(httpFileInfoList);
    }

    public void openFileList(List<String> fileNameList, boolean localFile) throws Exception {
        List<HttpFileInfo> httpFileInfoList = new ArrayList();
        for (String fileName : fileNameList) {
            httpFileInfoList.add(new HttpFileInfo(0, fileName));
        }
        createHttpFileReader(localFile);
        httpFilesReader.setFileList(httpFileInfoList);
    }

    public void openFileInfoList(List<HttpFileInfo> httpFileInfoList, boolean localFile) throws Exception {
        createHttpFileReader(localFile);
        httpFilesReader.setFileList(httpFileInfoList);
    }

    public long getLength() throws Exception {
        return httpFilesReader.getTotalFileSize();
    }

    public long readContent(OutputStream output, long startPos, long endPos) throws Exception {
        long totalFileSize = getLength();
        if (totalFileSize == 0) {
            return 0;
        }
        long readed = 0;
        if (startPos < endPos) {
            long len = endPos - startPos + 1;
            readed = httpFilesReader.readBuffer(startPos, len, output);
            if (readed != len) {
                logger.info("something wrong in prcoess read buffer");
            }
        }
        logger.debug("readed:" + readed);
        return readed;
    }
}
