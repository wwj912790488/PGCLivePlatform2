package com.arcvideo.pgcliveplatformserver.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;

/**
 * Created by slw on 2017/2/6.
 */
public class DatatableUtil {
    static public Integer getIntFirstValue(MultiValueMap<String, String> parametres, String key) {
        Integer res = null;
        String s = parametres.getFirst(key);
        if (StringUtils.isNotEmpty(s)) {
            res = Integer.parseInt(s);
        }
        return res;
    }

    static public Long getLongFirstValue(MultiValueMap<String, String> parametres, String key) {
        Long res = null;
        String s = parametres.getFirst(key);
        if (StringUtils.isNotEmpty(s)) {
            res = Long.parseLong(s);
        }
        return res;
    }

    static public String[] getStrArr(MultiValueMap<String, String> parametres, String key) {
        String[] checkeds = null;
        String str = parametres.getFirst(key);
        if (StringUtils.isNotEmpty(str)) {
            checkeds = str.trim().split(",");
        }
        return checkeds;
    }

    static public <T extends Enum<T>> T getEnumFirstValue(MultiValueMap<String, String> parametres, String key, Class<T> enumType) {
        T customer = null;
        String str = parametres.getFirst(key);
        if (StringUtils.isNotEmpty(str)) {
            try {
                customer = Enum.valueOf(enumType, str);
            } catch (Exception e) {
            }
        }
        return customer;
    }

    static public <T extends Enum<T>> T getEnumValue(String key,  Class<T> enumType) {
        T customer = null;
        if (StringUtils.isNotEmpty(key)) {
            try {
                customer = Enum.valueOf(enumType, key);
            } catch (Exception e) {
            }
        }
        return customer;
    }
}
