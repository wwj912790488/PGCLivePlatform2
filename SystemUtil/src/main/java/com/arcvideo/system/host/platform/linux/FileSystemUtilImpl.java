package com.arcvideo.system.host.platform.linux;

import com.arcvideo.system.host.FileSystemUtil;
import com.arcvideo.system.model.FileStore;
import com.arcvideo.system.util.ExecutingCommand;
import com.arcvideo.system.util.FileUtil;
import com.arcvideo.system.util.ParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileSystemUtilImpl implements FileSystemUtil {
    private static final Logger LOG = LoggerFactory.getLogger(FileSystemUtilImpl.class);

    // Linux defines a set of virtual file systems
    private final List<String> pseudofs = Arrays.asList(new String[] { //
            "rootfs", // Minimal fs to support kernel boot
            "sysfs", // SysFS file system
            "proc", // Proc file system
            "devtmpfs", // Dev temporary file system
            "devpts", // Dev pseudo terminal devices file system
            "securityfs", // Kernel security file system
            "cgroup", // Cgroup file system
            "pstore", // Pstore file system
            "hugetlbfs", // Huge pages support file system
            "configfs", // Config file system
            "selinuxfs", // SELinux file system
            "systemd-1", // Systemd file system
            "binfmt_misc", // Binary format support file system
            "mqueue", // Message queue file system
            "debugfs", // Debug file system
            "nfsd", // NFS file system
            "sunrpc", // Sun RPC file system
            "rpc_pipefs", // Sun RPC file system
            "fusectl", // FUSE control file system
            // NOTE: FUSE's fuseblk is not evalued because used as file system
            // representation of a FUSE block storage
            // "fuseblk" // FUSE block file system
            // "tmpfs", // Temporary file system
            // NOTE: tmpfs is evaluated apart, because Linux uses it for
            // RAMdisks
    });

    private final List<String> tmpfsPaths = Arrays.asList(new String[] { "/dev/shm", "/run", "/sys", "/proc" });

    private boolean listElementStartsWith(List<String> aList, String charSeq) {
        for (String match : aList) {
            if (charSeq.equals(match) || charSeq.startsWith(match + "/")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public FileStore[] getFileStores() {
        // Map uuids with device path as key
        Map<String, String> uuidMap = new HashMap<>();
        File uuidDir = new File("/dev/disk/by-uuid");
        if (uuidDir != null && uuidDir.listFiles() != null) {
            for (File uuid : uuidDir.listFiles()) {
                try {
                    // Store UUID as value with path (e.g., /dev/sda1) as key
                    uuidMap.put(uuid.getCanonicalPath(), uuid.getName().toLowerCase());
                } catch (IOException e) {
                    LOG.error("Couldn't get canonical path for {}. {}", uuid.getName(), e);
                }
            }
        }

        // List file systems
        List<FileStore> fsList = new ArrayList<>();

        // Parse /proc/self/mounts to get fs types
        List<String> mounts = FileUtil.readFile("/proc/self/mounts");
        for (String mount : mounts) {
            String[] split = mount.split(" ");
            // As reported in fstab(5) manpage, struct is:
            // 1st field is volume name
            // 2nd field is path with spaces escaped as \040
            // 3rd field is fs type
            // 4th field is mount options (ignored)
            // 5th field is used by dump(8) (ignored)
            // 6th field is fsck order (ignored)
            if (split.length < 6) {
                continue;
            }

            // Exclude pseudo file systems
            String path = split[1].replaceAll("\\\\040", " ");
            String type = split[2];
            if (this.pseudofs.contains(type) || path.equals("/dev") || listElementStartsWith(this.tmpfsPaths, path)) {
                continue;
            }

            String name = split[0].replaceAll("\\\\040", " ");
            if (path.equals("/")) {
                name = "/";
            }
            String volume = split[0].replaceAll("\\\\040", " ");
            String uuid = ParseUtil.getOrUseDefault(uuidMap, split[0], "");
            long totalSpace = new File(path).getTotalSpace();
            long usableSpace = new File(path).getUsableSpace();

            String description;
            if (volume.startsWith("/dev")) {
                description = "Local Disk";
            } else if (volume.equals("tmpfs")) {
                description = "Ram Disk";
            } else if (type.startsWith("nfs") || type.equals("cifs") || type.equals("oss")) {
                description = "Network Disk";
            } else {
                description = "Mount Point";
            }

            FileStore osStore = new FileStore(name, volume, path, description, type, uuid, usableSpace, totalSpace);
            fsList.add(osStore);
        }

        return fsList.toArray(new FileStore[fsList.size()]);
    }

    @Override
    public long getOpenFileDescriptors() {
        return getFileDescriptors(0);
    }

    @Override
    public long getMaxFileDescriptors() {
        return getFileDescriptors(2);
    }

    private long getFileDescriptors(int index) {
        String filename = "/proc/sys/fs/file-nr";
        if (index < 0 || index > 2) {
            throw new IllegalArgumentException("Index must be between 0 and 2.");
        }
        List<String> osDescriptors = FileUtil.readFile(filename);
        if (!osDescriptors.isEmpty()) {
            String[] splittedLine = osDescriptors.get(0).split("\\D+");
            return ParseUtil.parseLongOrDefault(splittedLine[index], 0L);
        }
        return 0L;
    }
}
