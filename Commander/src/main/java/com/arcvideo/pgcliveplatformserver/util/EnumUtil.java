package com.arcvideo.pgcliveplatformserver.util;

public class EnumUtil {

    public static <T extends Enum<T>> T indexOf(Class<T> clazz, int ordinal){
        return (T) clazz.getEnumConstants()[ordinal];
    }
}
