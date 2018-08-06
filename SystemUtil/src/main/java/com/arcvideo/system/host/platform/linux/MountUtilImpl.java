package com.arcvideo.system.host.platform.linux;

import com.arcvideo.system.host.MountUtil;
import com.arcvideo.system.util.ExecutingCommand;
import com.arcvideo.system.util.StringHelper;
import com.sun.jna.platform.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.arcvideo.system.model.Storage;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MountUtilImpl implements MountUtil {
    private String mountBaseDir = "/mnt/data/remote";
    private static final Logger logger = LoggerFactory.getLogger(MountUtilImpl.class);

    @Override
    public String getMountBaseDir() {
        return mountBaseDir;
    }

    @Override
    public void setMountBaseDir(String mountBaseDir) {
        this.mountBaseDir = mountBaseDir;
    }

    @Override
    public List<Storage> getMountedStorageList(final String... mountTypeFilters) {
        // get cifs nfs mounted
        List arrayList = new ArrayList();
        for (String mountType : mountTypeFilters) {
            if (mountType.equals("oss")) {
                String shellCmd = "ps aux | grep -v grep | grep arcfs | awk '$11!=\"grep\"{sub(/^oss:\\/\\//,\"\",$13);sub(/.*@/,\"\",$13);print \"//\"$13,$12}'";
                List<String> oss_mounted = ExecutingCommand.runShellNative(shellCmd);
                for (String each : oss_mounted) {
                    String[] datas = each.split(" ");
                    String remotePath = datas[0];
                    if (remotePath.endsWith("/")) {
                        remotePath = remotePath.substring(0, remotePath.length() - 1);
                    }
                    Storage storage = new Storage(getStorageName(datas[1]), remotePath, mountType);
                    arrayList.add(storage);
                }
            }
            String shellCmd = "mount -l | awk '$(NF-1)~/^(" + mountType + ")/{print $1,$3}'";
            List<String> mountedResults = ExecutingCommand.runShellNative(shellCmd);
            for (String mountedResult : mountedResults) {
                String[] datas = mountedResult.split(" ");
                Storage storage = new Storage(getStorageName(datas[1]), datas[0], mountType);
                arrayList.add(storage);
            }
        }
        return arrayList;
    }

    @Override
    public boolean mountStorage(Storage storage) {
        String umountDupMount = String.format("count=$(mount -l | awk '{print $3}' | grep -E \"^%s$\" | wc -l);while [ $count -gt 1 ];do ((count--));umount %s;done",
                localMountDir(storage), localMountDir(storage));
        ExecutingCommand.runShellNative(umountDupMount);
        if (isMounted(storage)) {
            return true;
        }

        String remoteDst = storage.getPath();
        String localdir = localMountDir(storage);

        File fldir = new File(localdir);
        if (!fldir.exists()) {
            fldir.mkdirs();
        }

        StringBuilder cmd = new StringBuilder();
        if (storage.getType().equals("oss")) {
            // arcfs process down, but mount -l exist.
            ExecutingCommand.runShellNative(String.format("umount %s", localMountDir(storage)));
            if (StringHelper.isNotEmpty(storage.getUser())) {
                remoteDst = remoteDst.replace("//", "");
                remoteDst = String.format("oss://%s:%s@", storage.getUser(), storage.getPassword()) + remoteDst;
            }
            else {
                remoteDst = "oss:" + remoteDst;
            }
            cmd.append("arcfs ").append(localdir).append(" ").append(getPathWithAppendSuffix(remoteDst));
            cmd.append(" > /dev/null 2>&1 &");
        }
        else {
            cmd.append("mount -t ").append(storage.getType()).append(' ')
                    .append(remoteDst.indexOf(' ') == -1 ? remoteDst : ('"' + remoteDst + '"'));
            cmd.append(' ').append(localdir);

            StringBuilder options = new StringBuilder();
            if (!StringHelper.isEmpty(storage.getUser())) {
                AppendOptions(options, "username=" + storage.getUser());
            }

            if (!StringHelper.isEmpty(storage.getPassword())) {
                AppendOptions(options, "password=" + storage.getPassword());
            }
            else {
                if (storage.getType().equalsIgnoreCase("cifs")) {
                    AppendOptions(options, "password=' '");
                }
            }

            if (StringHelper.isNotEmpty(storage.getOptions())) {
                AppendOptions(options, storage.getOptions());
            }
            cmd.append(options);
            cmd.append("&&");
            cmd.append("(" + umountDupMount + ")");
        }

        ExecutingCommand.runShellNative(cmd.toString());
        if (isMounted(storage)) {
            return true;
        }
        return false;
    }

    private void AppendOptions(StringBuilder options, String option) {
        boolean isAppendFirstOption = options.toString().isEmpty();
        if (isAppendFirstOption) {
            options.append(" -o ");
        }

        if (!isAppendFirstOption) {
            options.append(",");
        }
        options.append(option);
    }

    @Override
    public boolean unmountStorage(Storage storage, boolean bRemoveFolder) {
        if (isMounted(storage)) {
            String mountedDir = localMountDir(storage);
            try {
                List<String> stringList = ExecutingCommand.runShellNativeWithException(String.format("umount %s", mountedDir));
            } catch (IOException e) {
                if (storage.getType().equals("oss")) {
                    // kill arcfs process before umount oss ;
                    String strShell = String.format("arcfs_pid=$(ps aux | grep -v grep | grep arcfs|awk '$(NF-1)==\"%s\"{print $2}');[ $arcfs_pid ] && kill -9 $arcfs_pid && umount %s",
                            mountedDir, mountedDir);
                    ExecutingCommand.runShellNative(strShell);
                }
                else {
                    ExecutingCommand.runShellNative(String.format("umount %s", storage.getPath()));
                }
            }
        }
        if(isMounted(storage)){
            ExecutingCommand.runShellNative(String.format("umount -l %s", storage.getPath()));
            if(isMounted(storage)){
                return false ;
            }

        }
        if (bRemoveFolder) {
            File file = new File(localMountDir(storage));
            file.delete();
        }
        return true;
    }

    private static String getPathWithAppendSuffix(String path) {
        return path.endsWith("/") ? path : (path + "/");
    }

    private String getStorageName(String mountdir) {
        mountdir = mountdir.replaceFirst(mountBaseDir, "");
        if (mountdir.startsWith("/")) {
            mountdir = mountdir.substring(1);
        }
        return mountdir;
    }

    private String localMountDir(Storage storage) {
        String p = getPathWithAppendSuffix(getMountBaseDir());
        p += storage.getName();
        return p;
    }

    @Override
    public boolean isMounted(Storage storage) {
        List<Storage> mountedStorageList = getMountedStorageList(storage.getType());
        for (Storage mountedStorage : mountedStorageList) {
            String mount1 = "";
            String mount2 =  getPathWithAppendSuffix(mountedStorage.getPath());
            if(storage.getType().equals("oss")){
                mount1 = getPathWithAppendSuffix("//oss:"+ storage.getPath().split("/")[0].trim());
            }else {
                mount1 = getPathWithAppendSuffix(storage.getPath());
            }
            if (mount1.equals(mount2) && storage.getName().equals(mountedStorage.getName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSameMountRemotePath(String src, String dst) {
        String src1 = getPathWithAppendSuffix(src);
        String dst1 = getPathWithAppendSuffix(dst);
        return src1.equals(dst1);
    }
}
