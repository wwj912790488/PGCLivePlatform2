package com.arcvideo.pgcliveplatformserver.model.supervisor;

import java.util.List;

/**
 * Created by zfl on 2018/4/23.
 */
public class SourceInfo {

    private String url;
    private String name;
    private String nioIpAndMask;

    public String getNioIpAndMask() {
        return nioIpAndMask;
    }

    public void setNioIpAndMask(String nioIpAndMask) {
        this.nioIpAndMask = nioIpAndMask;
    }

    private List<SupervisorProgram> data;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SupervisorProgram> getData() {
        return data;
    }

    public void setData(List<SupervisorProgram> data) {
        this.data = data;
    }
}
