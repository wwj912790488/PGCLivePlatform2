package com.arcvideo.system.host;

public interface MemoryUtil {
    long getTotal();
    long getAvailable();
    long getSwapTotal();
    long getSwapUsed();
}
