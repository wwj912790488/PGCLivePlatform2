package com.arcvideo.system.host;

import com.arcvideo.system.model.Storage;

import java.util.List;

public interface MountUtil {
    void setMountBaseDir(String mountBaseDir);
    String getMountBaseDir();
    List<Storage> getMountedStorageList(final String... mountTypeFilters);
    boolean mountStorage(Storage storage);
    boolean unmountStorage(Storage storage, boolean bRemoveFolder);
    boolean isMounted(Storage storage);
}
