package com.arcvideo.pgcliveplatformserver.util;

/**
 * Created by slw on 2018/6/15.
 */
public class IPUtil {

    public static long ipToLong(String strIp) throws Exception {
        long[] ip = new long[4];
        // 先找到IP地址字符串中.的位置
        int position1 = strIp.indexOf(".");
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);
        // 将每个.之间的字符串转换成整型
        ip[0] = Long.parseLong(strIp.substring(0, position1));
        ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIp.substring(position3 + 1));
        if ((ip[0] > 255 || ip[0] < 0) || (ip[1] > 255 || ip[1] < 0) || (ip[2] > 255 || ip[2] < 0) || (ip[3] > 255 || ip[3] < 0)) {
            throw new Exception("ip parse error");
        }
        Long result = (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
        return result;
    }

    public static String longToIP(long longIp) {
        StringBuffer sb = new StringBuffer("");
        // 直接右移24位
        sb.append(String.valueOf((longIp >>> 24)));
        sb.append(".");
        // 将高8位置0，然后右移16位
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        // 将高16位置0，然后右移8位
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
        sb.append(".");
        // 将高24位置0
        sb.append(String.valueOf((longIp & 0x000000FF)));
        return sb.toString();
    }

    public static String randomLongIp(String ipBegin, String ipEnd, int portBegin, int portEnd) throws Exception {
        long longIpBgn = ipToLong(ipBegin);
        long longIpEnd = ipToLong(ipEnd);
        long randomIp = (long)(Math.random() * (longIpEnd - longIpBgn + 1)) + longIpBgn;
        int randomPort = (int)(Math.random() * (portEnd - portBegin + 1) + portBegin);

        return String.format("udp://%s:%d", longToIP(randomIp), randomPort);
    }
}
