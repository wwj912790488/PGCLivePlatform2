package com.arcvideo.system.host.platform.linux;

import com.arcvideo.system.host.common.AbstractOSVersionUtilInfoEx;
import com.arcvideo.system.util.ExecutingCommand;
import com.arcvideo.system.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class OSVersionUtilInfoEx extends AbstractOSVersionUtilInfoEx {
    private static final Logger LOG = LoggerFactory.getLogger(OSVersionUtilInfoEx.class);

    public OSVersionUtilInfoEx() {
        this(null, null);
    }

    protected OSVersionUtilInfoEx(String versionId, String codeName) {
        setVersion(versionId);
        setCodeName(codeName);
        if (getVersion() == null) {
            setVersionFromReleaseFiles();
        }
        if (getCodeName() == null) {
            setCodeName("");
        }
        List<String> procVersion = null;
        procVersion = FileUtil.readFile("/proc/version");
        if (!procVersion.isEmpty()) {
            String[] split = procVersion.get(0).split("\\s+");
            for (String s : split) {
                if (!"Linux".equals(s) && !"version".equals(s)) {
                    setBuildNumber(s);
                    return;
                }
            }
        }
        setBuildNumber("");
    }

    private void setVersionFromReleaseFiles() {
        if (readOsRelease()) {
            return;
        }

        if (execLsbRelease()) {
            return;
        }

        if (readLsbRelease()) {
            return;
        }

        String etcDistribRelease = OperatingSystemUtilImpl.getReleaseFilename();
        if (readDistribRelease(etcDistribRelease)) {
            return;
        }
        if (getVersion() == null) {
            setVersion(System.getProperty("os.version"));
        }
    }

    private boolean readOsRelease() {
        if (new File("/etc/os-release").exists()) {
            List<String> osRelease = FileUtil.readFile("/etc/os-release");
            for (String line : osRelease) {
                if (line.startsWith("VERSION=")) {
                    LOG.debug("os-release: {}", line);
                    line = line.replace("VERSION=", "").replaceAll("^\"|\"$", "").trim();
                    String[] split = line.split("[()]");
                    if (split.length <= 1) {
                        split = line.split(", ");
                    }
                    if (split.length > 0) {
                        this.version = split[0].trim();
                    }
                    if (split.length > 1) {
                        this.codeName = split[1].trim();
                    }
                } else if (line.startsWith("VERSION_ID=") && this.version == null) {
                    LOG.debug("os-release: {}", line);
                    this.version = line.replace("VERSION_ID=", "").replaceAll("^\"|\"$", "").trim();
                }
            }
        }
        return this.version != null;
    }

    private boolean execLsbRelease() {
        for (String line : ExecutingCommand.runShellNative("lsb_release -a")) {
            if (line.startsWith("Description:")) {
                LOG.debug("lsb_release -a: {}", line);
                line = line.replace("Description:", "").trim();
                if (line.contains(" release ")) {
                    this.version = parseRelease(line, " release ");
                }
            } else if (line.startsWith("Release:") && this.version == null) {
                LOG.debug("lsb_release -a: {}", line);
                this.version = line.replace("Release:", "").trim();
            } else if (line.startsWith("Codename:") && this.codeName == null) {
                LOG.debug("lsb_release -a: {}", line);
                this.codeName = line.replace("Codename:", "").trim();
            }
        }
        return this.version != null;
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
                        this.version = parseRelease(line, " release ");
                    }
                } else if (line.startsWith("DISTRIB_RELEASE=") && this.version == null) {
                    LOG.debug("lsb-release: {}", line);
                    this.version = line.replace("DISTRIB_RELEASE=", "").replaceAll("^\"|\"$", "").trim();
                } else if (line.startsWith("DISTRIB_CODENAME=") && this.codeName == null) {
                    LOG.debug("lsb-release: {}", line);
                    this.codeName = line.replace("DISTRIB_CODENAME=", "").replaceAll("^\"|\"$", "").trim();
                }
            }
        }
        return this.version != null;
    }

    private boolean readDistribRelease(String filename) {
        if (new File(filename).exists()) {
            List<String> osRelease = FileUtil.readFile(filename);
            // Search for Distrib release x.x (Codename)
            for (String line : osRelease) {
                LOG.debug("{}: {}", filename, line);
                if (line.contains(" release ")) {
                    this.version = parseRelease(line, " release ");
                    // If this parses properly we're done
                    break;
                } else if (line.contains(" VERSION ")) {
                    this.version = parseRelease(line, " VERSION ");
                    // If this parses properly we're done
                    break;
                }
            }
        }
        return this.version != null;
    }

    private String parseRelease(String line, String splitLine) {
        String[] split = line.split(splitLine);
        if (split.length > 1) {
            split = split[1].split("[()]");
            if (split.length > 0) {
                this.version = split[0].trim();
            }
            if (split.length > 1) {
                this.codeName = split[1].trim();
            }
        }
        return this.version;
    }
}
