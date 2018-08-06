package com.arcvideo.pgcliveplatformserver.util;

import java.io.IOException;

/**
 * Created by zfl on 2018/6/8.
 */
public class DecodeUtils {

    /**
     * Base64解码
     *
     * @param str
     * @return
     */
    public static String decodeString(String str) {
        sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
        try {
            return new String(dec.decodeBuffer(str));
        } catch (IOException io) {
            throw new RuntimeException(io.getMessage(), io.getCause());
        }
    }
}
