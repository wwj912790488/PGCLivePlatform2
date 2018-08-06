package com.arcvideo.system.host.platform.linux;

import com.arcvideo.system.host.FileSystemUtil;
import com.arcvideo.system.host.ProcessUtil;
import com.arcvideo.system.host.common.AbstractOperatingSystemUtil;
import com.arcvideo.system.util.ExecutingCommand;
import com.arcvideo.system.util.FileUtil;
import com.arcvideo.system.util.ParseUtil;
import com.arcvideo.system.util.ProcUtil;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OperatingSystemUtilImpl extends AbstractOperatingSystemUtil {
    private static final Logger LOG = LoggerFactory.getLogger(OperatingSystemUtilImpl.class);

    protected String versionId;

    protected String codeName;

    private final int memoryPageSize;

    private static int getMemoryPageSize() {
        try {
            return Libc.INSTANCE.getpagesize();
        } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
            LOG.error("Failed to get the memory page size.", e);
        }
        // default to 4K if the above call fails
        return 4096;
    }

    public OperatingSystemUtilImpl() {
        this.manufacturer = "GNU/Linux";
        setFamilyFromReleaseFiles();
        // The above call may also populate versionId and codeName
        // to pass to version constructor
        this.version = new OSVersionUtilInfoEx(this.versionId, this.codeName);
        this.memoryPageSize = getMemoryPageSize();
    }

    @Override
    public FileSystemUtil getFileSystem() {
        return new FileSystemUtilImpl();
    }

    @Override
    public ProcessUtil[] getProcesses(int limit, ProcessSort sort) {
        List<ProcessUtil> procs = new ArrayList<>();
        File[] pids = ProcUtil.getPidFiles();
        // now for each file (with digit name) get process info
        for (File pid : pids) {
            ProcessUtil proc = getProcess(ParseUtil.parseIntOrDefault(pid.getName(), 0));
            if (proc != null) {
                procs.add(proc);
            }
        }
        List<ProcessUtil> sorted = processSort(procs, limit, sort);
        return sorted.toArray(new ProcessUtil[sorted.size()]);
    }

    @Override
    public ProcessUtil getProcess(int pid) {
        String[] split = FileUtil.getSplitFromFile(String.format("/proc/%d/stat", pid));
        if (split.length < 24) {
            return null;
        }
        String path = "";
        Pointer buf = new Memory(1024);
        int size = Libc.INSTANCE.readlink(String.format("/proc/%d/exe", pid), buf, 1023);
        if (size > 0) {
            path = buf.getString(0).substring(0, size);
        }
        Map<String, String> io = FileUtil.getKeyValueMapFromFile(String.format("/proc/%d/io", pid), ":");
        return new ProcessUtilImpl(
                split[1].replaceFirst("\\(", "").replace(")", ""), // name
                // See man proc for how to parse /proc/[pid]/stat
                path, // path
                split[2].charAt(0), // state, one of RSDZTW
                pid, // also split[0] but we already have
                ParseUtil.parseIntOrDefault(split[3], 0), // ppid
                ParseUtil.parseIntOrDefault(split[19], 0), // thread count
                ParseUtil.parseIntOrDefault(split[17], 0), // priority
                ParseUtil.parseLongOrDefault(split[22], 0L), // VSZ
                ParseUtil.parseLongOrDefault(split[23], 0L) * memoryPageSize, // RSS pages * page_size
                // The below values are in jiffies
                ParseUtil.parseLongOrDefault(split[14], 0L), // kernelTime
                ParseUtil.parseLongOrDefault(split[13], 0L), // userTime
                ParseUtil.parseLongOrDefault(split[21], 0L), // startTime (after
                                                             // uptime)
                // See man proc for how to parse /proc/[pid]/io
                ParseUtil.parseLongOrDefault(ParseUtil.getOrUseDefault(io, "read_bytes", ""), 0L),
                ParseUtil.parseLongOrDefault(ParseUtil.getOrUseDefault(io, "write_bytes", ""), 0L),
                System.currentTimeMillis() //
        );
    }

    @Override
    public int getProcessId() {
        return Libc.INSTANCE.getpid();
    }

    @Override
    public int getProcessCount() {
        return ProcUtil.getPidFiles().length;
    }

    @Override
    public int getThreadCount() {
        try {
            Libc.Sysinfo info = new Libc.Sysinfo();
            if (0 != Libc.INSTANCE.sysinfo(info)) {
                LOG.error("Failed to get process thread count. Error code: " + Native.getLastError());
                return 0;
            }
            return info.procs;
        } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
            LOG.error("Failed to get procs from sysinfo. {}", e);
        }
        return 0;
    }

    private void setFamilyFromReleaseFiles() {
        if (this.family == null) {
            if (readOsRelease()) {
                return;
            }

            if (execLsbRelease()) {
                return;
            }

            if (readLsbRelease()) {
                return;
            }

            String etcDistribRelease = getReleaseFilename();
            if (readDistribRelease(etcDistribRelease)) {
                return;
            }
            this.family = filenameToFamily(etcDistribRelease.replace("/etc/", "").replace("release", "")
                    .replace("version", "").replace("-", "").replace("_", ""));
        }
    }

    private boolean readOsRelease() {
        if (new File("/etc/os-release").exists()) {
            List<String> osRelease = FileUtil.readFile("/etc/os-release");
            // Search for NAME=
            for (String line : osRelease) {
                if (line.startsWith("VERSION=")) {
                    LOG.debug("os-release: {}", line);
                    // remove beginning and ending '"' characters, etc from
                    // VERSION="14.04.4 LTS, Trusty Tahr" (Ubuntu style)
                    // or VERSION="17 (Beefy Miracle)" (os-release doc style)
                    line = line.replace("VERSION=", "").replaceAll("^\"|\"$", "").trim();
                    String[] split = line.split("[()]");
                    if (split.length <= 1) {
                        // If no parentheses, check for Ubuntu's comma format
                        split = line.split(", ");
                    }
                    if (split.length > 0) {
                        this.versionId = split[0].trim();
                    }
                    if (split.length > 1) {
                        this.codeName = split[1].trim();
                    }
                } else if (line.startsWith("NAME=") && this.family == null) {
                    LOG.debug("os-release: {}", line);
                    // remove beginning and ending '"' characters, etc from
                    // NAME="Ubuntu"
                    this.family = line.replace("NAME=", "").replaceAll("^\"|\"$", "").trim();
                } else if (line.startsWith("VERSION_ID=") && this.versionId == null) {
                    LOG.debug("os-release: {}", line);
                    // remove beginning and ending '"' characters, etc from
                    // VERSION_ID="14.04"
                    this.versionId = line.replace("VERSION_ID=", "").replaceAll("^\"|\"$", "").trim();
                }
            }
        }
        return this.family != null;
    }

    private boolean execLsbRelease() {
        for (String line : ExecutingCommand.runShellNative("lsb_release -a")) {
            if (line.startsWith("Description:")) {
                LOG.debug("lsb_release -a: {}", line);
                line = line.replace("Description:", "").trim();
                if (line.contains(" release ")) {
                    this.family = parseRelease(line, " release ");
                }
            } else if (line.startsWith("Distributor ID:") && this.family == null) {
                LOG.debug("lsb_release -a: {}", line);
                this.family = line.replace("Distributor ID:", "").trim();
            } else if (line.startsWith("Release:") && this.versionId == null) {
                LOG.debug("lsb_release -a: {}", line);
                this.versionId = line.replace("Release:", "").trim();
            } else if (line.startsWith("Codename:") && this.codeName == null) {
                LOG.debug("lsb_release -a: {}", line);
                this.codeName = line.replace("Codename:", "").trim();
            }
        }
        return this.family != null;
    }

    private boolean readLsbRelease() {
        if (new File("/etc/lsb-release").exists()) {
            List<String> osRelease = FileUtil.readFile("/etc/lsb-release");
            // Search for NAME=
            for (String line : osRelease) {
                if (line.startsWith("DISTRIB_DESCRIPTION=")) {
                    LOG.debug("lsb-release: {}", line);
                    line = line.replace("DISTRIB_DESCRIPTION=", "").replaceAll("^\"|\"$", "").trim();
                    if (line.contains(" release ")) {
                        this.family = parseRelease(line, " release ");
                    }
                } else if (line.startsWith("DISTRIB_ID=") && this.family == null) {
                    LOG.debug("lsb-release: {}", line);
                    this.family = line.replace("DISTRIB_ID=", "").replaceAll("^\"|\"$", "").trim();
                } else if (line.startsWith("DISTRIB_RELEASE=") && this.versionId == null) {
                    LOG.debug("lsb-release: {}", line);
                    this.versionId = line.replace("DISTRIB_RELEASE=", "").replaceAll("^\"|\"$", "").trim();
                } else if (line.startsWith("DISTRIB_CODENAME=") && this.codeName == null) {
                    LOG.debug("lsb-release: {}", line);
                    this.codeName = line.replace("DISTRIB_CODENAME=", "").replaceAll("^\"|\"$", "").trim();
                }
            }
        }
        return this.family != null;
    }

    private boolean readDistribRelease(String filename) {
        if (new File(filename).exists()) {
            List<String> osRelease = FileUtil.readFile(filename);
            // Search for Distrib release x.x (Codename)
            for (String line : osRelease) {
                LOG.debug("{}: {}", filename, line);
                if (line.contains(" release ")) {
                    this.family = parseRelease(line, " release ");
                    // If this parses properly we're done
                    break;
                } else if (line.contains(" VERSION ")) {
                    this.family = parseRelease(line, " VERSION ");
                    // If this parses properly we're done
                    break;
                }
            }
        }
        return this.family != null;
    }

    private String parseRelease(String line, String splitLine) {
        String[] split = line.split(splitLine);
        String family = split[0].trim();
        if (split.length > 1) {
            split = split[1].split("[()]");
            if (split.length > 0) {
                this.versionId = split[0].trim();
            }
            if (split.length > 1) {
                this.codeName = split[1].trim();
            }
        }
        return family;
    }

    protected static String getReleaseFilename() {
        File etc = new File("/etc");
        File[] matchingFiles = etc.listFiles(new FileFilter() {
                                                 @Override
                                                 public boolean accept(File f) {
                                                     return (f.getName().endsWith("-release") || f.getName().endsWith("-version")
                                                             || f.getName().endsWith("_release") || f.getName().endsWith("_version"))
                                                             && !(f.getName().endsWith("os-release") || f.getName().endsWith("lsb-release"));
                                                 }
                                             });

        if (matchingFiles != null && matchingFiles.length > 0) {
            return matchingFiles[0].getPath();
        }
        if (new File("/etc/release").exists()) {
            return "/etc/release";
        }
        // If all else fails, try this
        return "/etc/issue";
    }

    private static String filenameToFamily(String name) {
        switch (name.toLowerCase()) {
        // Handle known special cases
        case "":
            return "Solaris";
        case "blackcat":
            return "Black Cat";
        case "bluewhite64":
            return "BlueWhite64";
        case "e-smith":
            return "SME Server";
        case "eos":
            return "FreeEOS";
        case "hlfs":
            return "HLFS";
        case "lfs":
            return "Linux-From-Scratch";
        case "linuxppc":
            return "Linux-PPC";
        case "meego":
            return "MeeGo";
        case "mandakelinux":
            return "Mandrake";
        case "mklinux":
            return "MkLinux";
        case "nld":
            return "Novell Linux Desktop";
        case "novell":
        case "SuSE":
            return "SUSE Linux";
        case "pld":
            return "PLD";
        case "redhat":
            return "Red Hat Linux";
        case "sles":
            return "SUSE Linux ES9";
        case "sun":
            return "Sun JDS";
        case "synoinfo":
            return "Synology";
        case "tinysofa":
            return "Tiny Sofa";
        case "turbolinux":
            return "TurboLinux";
        case "ultrapenguin":
            return "UltraPenguin";
        case "va":
            return "VA-Linux";
        case "vmware":
            return "VMWareESX";
        case "yellowdog":
            return "Yellow Dog";

        // /etc/issue will end up here:
        case "issue":
            return "Unknown";
        // If not a special case just capitalize first letter
        default:
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
    }
}
