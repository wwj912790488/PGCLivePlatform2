package com.arcvideo.system.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {
    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {
    }

    public static List<String> readFile(String filename) {
        return readFile(filename, true);
    }

    public static List<String> readFile(String filename, boolean reportError) {
        if (new File(filename).exists()) {
            LOG.debug("Reading file {}", filename);
            try {
                return Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
            } catch (IOException e) {
                if (reportError) {
                    LOG.error("Error reading file {}. {}", filename, e);
                }
            }
        } else if (reportError) {
            LOG.warn("File not found: {}", filename);
        }
        return new ArrayList<>();
    }

    public static long getLongFromFile(String filename) {
        LOG.debug("Reading file {}", filename);
        List<String> read = FileUtil.readFile(filename, false);
        if (!read.isEmpty()) {
            LOG.trace("Read {}", read.get(0));
            return ParseUtil.parseLongOrDefault(read.get(0), 0L);
        }
        return 0L;
    }

    public static int getIntFromFile(String filename) {
        LOG.debug("Reading file {}", filename);
        try {
            List<String> read = FileUtil.readFile(filename, false);
            if (!read.isEmpty()) {
                LOG.trace("Read {}", read.get(0));
                return Integer.parseInt(read.get(0));
            }
        } catch (NumberFormatException ex) {
            LOG.debug("Unable to read value from {}. {}", filename, ex);
        }
        return 0;
    }

    public static String getStringFromFile(String filename) {
        LOG.debug("Reading file {}", filename);
        List<String> read = FileUtil.readFile(filename, false);
        if (!read.isEmpty()) {
            LOG.trace("Read {}", read.get(0));
            return read.get(0);
        }
        return "";
    }

    public static String[] getSplitFromFile(String filename) {
        LOG.debug("Reading file {}", filename);
        List<String> read = FileUtil.readFile(filename, false);
        if (!read.isEmpty()) {
            LOG.trace("Read {}", read.get(0));
            return read.get(0).split("\\s+");
        }
        return new String[0];
    }

    public static Map<String, String> getKeyValueMapFromFile(
            String filename,
            String separator) {
        Map<String, String> map = new HashMap<>();
        LOG.debug("Reading file {}", filename);
        List<String> lines = FileUtil.readFile(filename, false);
        for (String line : lines) {
            String[] parts = line.split(separator);
            if (parts.length == 2) {
                map.put(parts[0], parts[1].trim());
            }
        }
        return map;
    }
}