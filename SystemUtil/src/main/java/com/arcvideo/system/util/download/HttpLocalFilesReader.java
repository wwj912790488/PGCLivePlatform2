package com.arcvideo.system.util.download;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class HttpLocalFilesReader extends AbstractHttpFilesReader {
    private static final Logger logger = LoggerFactory.getLogger(HttpLocalFilesReader.class);

    @Override
    public void setFileList(List<HttpFileInfo> httpFileInfoList) throws Exception {
        for (HttpFileInfo httpFileInfo : httpFileInfoList) {
            File file = FileUtils.getFile(httpFileInfo.getFileName());
            this.httpFileInfoList.add(new HttpFileInfo(file.length(), httpFileInfo.getFileName()));
        }
    }

    @Override
    public long read(HttpFileInfo httpFileInfo, long pos, long len, OutputStream outputStream) throws Exception {
        long bufferReaded = 0;
        try (InputStream inputStream = new FileInputStream(FileUtils.getFile(httpFileInfo.getFileName()))) {
            inputStream.skip(pos);
            bufferReaded = readData(inputStream, pos, len, outputStream);
        }
        return bufferReaded;
    }
}
