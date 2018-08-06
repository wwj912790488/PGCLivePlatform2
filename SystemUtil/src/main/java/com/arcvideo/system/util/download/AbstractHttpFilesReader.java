package com.arcvideo.system.util.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHttpFilesReader implements HttpFilesReader {
    private static final Logger logger = LoggerFactory.getLogger(AbstractHttpFilesReader.class);

    protected List<HttpFileInfo> httpFileInfoList = new ArrayList<>();

    protected static final int READBUFFERSIZE = 32768;

    protected final byte[] copyBuffer = new byte[READBUFFERSIZE];

    @Override
    public long getTotalFileSize() throws Exception {
        long fileSize = 0;
        for (HttpFileInfo httpFileInfo : httpFileInfoList) {
            fileSize += httpFileInfo.getLength();
        }
        return fileSize;
    }

    @Override
    public long readBuffer(long pos, long len, OutputStream output) throws Exception {
        long totalReadedSize = 0;
        long curFileSize = 0;
        long nextFileSize = 0;
        long startReadPos = pos;
        int startFileIndex = 0;
        for (int i = 0; i < httpFileInfoList.size(); ++i) {
            nextFileSize += httpFileInfoList.get(i).getLength();
            if (pos >= curFileSize && pos < nextFileSize) {
                startFileIndex = i;
                startReadPos = pos - curFileSize;
                break;
            }
            curFileSize = nextFileSize;
        }
        for (; startFileIndex < httpFileInfoList.size(); ++startFileIndex) {
            long readLen = Math.min(len - totalReadedSize, httpFileInfoList.get(startFileIndex).getLength() - startReadPos);
            long readedSize = read(httpFileInfoList.get(startFileIndex),  startReadPos, readLen, output);
            totalReadedSize += readedSize;
            if (totalReadedSize >= len) {
                break;
            }
            startReadPos = 0;
        }
        return totalReadedSize;
    }

    protected long readData(InputStream inputStream, long pos, long len, OutputStream outputStream) throws Exception {
        long bufferReaded = 0;
        long bufferRemainSize = len;
        int bytesRead = 0;
        while ((bufferRemainSize > 0) && ((bytesRead = inputStream.read(copyBuffer)) != -1)) {
            if (bufferRemainSize > bytesRead) {
                outputStream.write(copyBuffer, 0, bytesRead);
                bufferRemainSize -= bytesRead;
                bufferReaded += bytesRead;
            } else {
                outputStream.write(copyBuffer, 0, (int) bufferRemainSize);
                bufferReaded += bufferRemainSize;
                bufferRemainSize = 0;
            }
            if (bufferRemainSize < 0) {
                logger.info("read file exception");
            }
        }
        return bufferReaded;
    }
}
