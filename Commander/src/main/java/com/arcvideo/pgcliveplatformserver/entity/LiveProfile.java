package com.arcvideo.pgcliveplatformserver.entity;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zfl on 2018/3/27.
 */
public class LiveProfile {

    public enum Status {
        AppleStreaming("Apple Live"),
        FlashStreaming("Flash"),
        UdpStreaming("UDP"),
        HttpStreaming("HTTP");

        private final String key;

        Status(String key) {
            this.key = key;
        }

        @JsonValue
        public String getKey() {
            return key;
        }

        public static List<Map<String, String>> getTypes() {
            List<Map<String, String>> types = new ArrayList<>();
            for (Status c : Status.values()) {
                Map<String, String> map = new HashMap<>();
                map.put("name", c.getKey());
                map.put("value", c.name());
                types.add(map);
            }
            return types;
        }
    }

    private String id;

    private String name;

    private Integer outputNum;

    private List<String> outputGroupTypes;

    public LiveProfile(String id, String name,Integer outputNum,List<String> types) {
        this.id = id;
        this.name = name;
        this.outputNum = outputNum;
        this.outputGroupTypes = types;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOutputNum() {
        return outputNum;
    }

    public void setOutputNum(Integer outputNum) {
        this.outputNum = outputNum;
    }

    public List<String> getOutputGroupTypes() {
        return outputGroupTypes;
    }

    public void setOutputGroupTypes(List<String> outputGroupTypes) {
        this.outputGroupTypes = outputGroupTypes;
    }
}
