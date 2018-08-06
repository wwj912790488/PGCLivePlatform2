package com.arcvideo.system.host;

import com.arcvideo.system.model.FileStore;

public interface FileSystemUtil {
    FileStore[] getFileStores();
    long getOpenFileDescriptors();
    long getMaxFileDescriptors();
}
