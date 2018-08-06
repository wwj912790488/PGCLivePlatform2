package com.arcvideo.pgcliveplatformserver.util;

/**
 * Created by slw on 2018/6/6.
 */
public class UriUtil {
    public static final String PROTOCOL_UDP = "udp";
    public static final String PROTOCOL_RTMP = "rtmp";
    public static final String PROTOCOL_HLS = "hls";

    public static String getProtocol(String uri) throws Exception {
        String protocol = null;
        int pos = uri.indexOf("://");
        if(pos == -1) {
            throw new Exception("The uri is not right");
        }

        String scheme = uri.substring(0, pos);
        String cased = scheme.toLowerCase();
        if (cased.startsWith("udp")) {
            protocol = PROTOCOL_UDP;
        }
        else if (cased.startsWith("rtmp")) {
            protocol = PROTOCOL_RTMP;
        }
        else if (cased.startsWith("http") && uri.endsWith(".m3u8")) {
            protocol = PROTOCOL_HLS;
        } else {
            throw new Exception("The protocol of uri is not supported");
        }
        return protocol;
    }
}
