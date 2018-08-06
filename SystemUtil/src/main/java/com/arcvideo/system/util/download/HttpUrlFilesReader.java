package com.arcvideo.system.util.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HttpUrlFilesReader extends AbstractHttpFilesReader {
    private static final Logger logger = LoggerFactory.getLogger(HttpUrlFilesReader.class);

    @Override
    public void setFileList(List<HttpFileInfo> httpFileInfoList) throws Exception {
        this.httpFileInfoList = httpFileInfoList;
    }

    @Override
    public long read(HttpFileInfo httpFileInfo, long pos, long len, OutputStream outputStream) throws Exception {
        long bufferReaded = 0;
        long startPos = httpFileInfo.getStartPos();
        URL url = new URL(httpFileInfo.getFileName());
        HttpURLConnection httpURLConnection = null;
        try {
            pos += startPos;
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Range", "bytes=" + pos + "-" + (pos + len - 1));
            int code = httpURLConnection.getResponseCode();
            try (InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream(), READBUFFERSIZE)) {
                bufferReaded = readData(inputStream, pos, len, outputStream);
            }
        }
        finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return bufferReaded;
    }
}
