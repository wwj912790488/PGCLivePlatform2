package com.arcvideo.system.host;

import com.arcvideo.system.model.Eth;

import java.io.IOException;
import java.util.List;

public interface NetworkUtil {
    List<Eth> findAllEths();
    void updateEth(Eth eth);
    int getEthUsedRate(String ethId) throws IOException;
    void bond(Eth master, String[] slaveEthId);
    List<Eth> findAllEths(boolean isbond);
}
