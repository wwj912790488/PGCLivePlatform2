package com.arcvideo.system.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ParseUtil.class);

    private static final String DEFAULT_LOG_MSG = "{} didn't parse. Returning default. {}";

    private static final Pattern HERTZ_PATTERN = Pattern.compile("(\\d+(.\\d+)?) ?([kMGT]?Hz).*");

    private static final Pattern VALID_HEX = Pattern.compile("[0-9a-fA-F]+");

    private static final Pattern DHMS = Pattern.compile("(?:(\\d+)-)?(?:(\\d+):)?(\\d+):(\\d+)(?:\\.(\\d+))?");

    private static final Pattern UUID_PATTERN = Pattern
            .compile(".*([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}).*");

    private static final String HZ = "Hz";
    private static final String KHZ = "kHz";
    private static final String MHZ = "MHz";
    private static final String GHZ = "GHz";
    private static final String THZ = "THz";
    private static final String PHZ = "PHz";

    private static final Map<String, Long> multipliers;

    static {
        multipliers = new HashMap<>();
        multipliers.put(HZ, 1L);
        multipliers.put(KHZ, 1000L);
        multipliers.put(MHZ, 1000000L);
        multipliers.put(GHZ, 1000000000L);
        multipliers.put(THZ, 1000000000000L);
        multipliers.put(PHZ, 1000000000000000L);
    }

    private ParseUtil() {
    }

    public static int parseLastInt(String s, int i) {
        try {
            return Integer.parseInt(parseLastString(s));
        } catch (NumberFormatException e) {
            LOG.trace(DEFAULT_LOG_MSG, s, e);
            return i;
        }
    }

    public static String parseLastString(String s) {
        String[] ss = s.split("\\s+");
        if (ss.length < 1) {
            return s;
        } else {
            return ss[ss.length - 1];
        }
    }

    public static byte[] hexStringToByteArray(String digits) {
        int len = digits.length();
        // Check if string is valid hex
        if (!VALID_HEX.matcher(digits).matches() || (len & 0x1) != 0) {
            LOG.warn("Invalid hexadecimal string: {}", digits);
            return new byte[0];
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) (Character.digit(digits.charAt(i), 16) << 4
                    | Character.digit(digits.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte[] stringToByteArray(String text, int length) {
        return Arrays.copyOf(text.getBytes(), length);
    }

    public static byte[] longToByteArray(long value, int valueSize, int length) {
        long val = value;
        // Convert the long to 8-byte BE representation
        byte[] b = new byte[8];
        for (int i = 7; i >= 0 && val != 0L; i--) {
            b[i] = (byte) val;
            val >>>= 8;
        }
        // Then copy the rightmost valueSize bytes
        // e.g., for an integer we want rightmost 4 bytes
        return Arrays.copyOfRange(b, 8 - valueSize, 8 + length - valueSize);
    }

    public static long strToLong(String str, int size) {
        return byteArrayToLong(str.getBytes(), size);
    }

    public static long byteArrayToLong(byte[] bytes, int size) {
        if (size > 8) {
            throw new IllegalArgumentException("Can't convert more than 8 bytes.");
        }
        if (size > bytes.length) {
            throw new IllegalArgumentException("Size can't be larger than array length.");
        }
        long total = 0L;
        for (int i = 0; i < size; i++) {
            total = total << 8 | bytes[i] & 0xff;
        }
        return total;
    }

    public static float byteArrayToFloat(byte[] bytes, int size, int fpBits) {
        return byteArrayToLong(bytes, size) / (float) (1 << fpBits);
    }

    public static String hexStringToString(String hexString) {
        // Odd length strings won't parse, return
        if (hexString.length() % 2 > 0) {
            return hexString;
        }
        int charAsInt;
        StringBuilder sb = new StringBuilder();
        try {
            for (int pos = 0; pos < hexString.length(); pos += 2) {
                charAsInt = Integer.parseInt(hexString.substring(pos, pos + 2), 16);
                if (charAsInt < 32 || charAsInt > 127) {
                    return hexString;
                }
                sb.append((char) charAsInt);
            }
        } catch (NumberFormatException e) {
            LOG.trace(DEFAULT_LOG_MSG, hexString, e);
            // Hex failed to parse, just return the existing string
            return hexString;
        }
        return sb.toString();
    }

    public static int parseIntOrDefault(String s, int defaultInt) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            LOG.trace(DEFAULT_LOG_MSG, s, e);
            return defaultInt;
        }
    }

    public static long parseLongOrDefault(String s, long defaultLong) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            LOG.trace(DEFAULT_LOG_MSG, s, e);
            return defaultLong;
        }
    }

    public static double parseDoubleOrDefault(String s, double defaultDouble) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            LOG.trace(DEFAULT_LOG_MSG, s, e);
            return defaultDouble;
        }
    }

    public static long parseDHMSOrDefault(String s, long defaultLong) {
        Matcher m = DHMS.matcher(s);
        if (m.matches()) {
            long milliseconds = 0L;
            if (m.group(1) != null) {
                milliseconds += parseLongOrDefault(m.group(1), 0L) * 86400000L;
            }
            if (m.group(2) != null) {
                milliseconds += parseLongOrDefault(m.group(2), 0L) * 3600000L;
            }
            milliseconds += parseLongOrDefault(m.group(3), 0L) * 60000L;
            milliseconds += parseLongOrDefault(m.group(4), 0L) * 1000L;
            milliseconds += 1000 * parseDoubleOrDefault("0." + m.group(5), 0d);
            return milliseconds;
        }
        return defaultLong;
    }

    public static String parseUuidOrDefault(String s, String defaultStr) {
        Matcher m = UUID_PATTERN.matcher(s.toLowerCase());
        if (m.matches()) {
            return m.group(1);
        }
        return defaultStr;
    }

    public static String getSingleQuoteStringValue(String line) {
        String[] split = line.split("'");
        if (split.length < 2) {
            return "";
        }
        return split[1];
    }

    public static int getFirstIntValue(String line) {
        String[] split = line.split("=");
        if (split.length < 2) {
            return 0;
        }
        return parseIntOrDefault(split[1].trim().split("\\s+")[0], 0);
    }

    public static long parseHertz(String hertz) {
        Matcher matcher = HERTZ_PATTERN.matcher(hertz.trim());
        if (matcher.find() && matcher.groupCount() == 3) {
            // Regexp enforces #(.#) format so no test for NFE required
            Double value = Double.valueOf(matcher.group(1)) * getOrUseDefault(multipliers, matcher.group(3), -1L);
            return value < 0d ? -1L : value.longValue();
        }
        return -1L;
    }

    public static <K, V> V getOrUseDefault(Map<K, V> map, Object key, V defaultValue) {
        V v;
        return (((v = map.get(key)) != null) || map.containsKey(key))
                ? v
                : defaultValue;
    }

}
