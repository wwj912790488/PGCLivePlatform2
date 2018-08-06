package com.arcvideo.pgcliveplatformserver.model.supervisor;

/**
 * Created by zfl on 2018/4/23.
 */
public class SupervisorProgram {
    private Long serviceId;
    private String proname;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getProname() {
        return proname;
    }

    public void setProname(String proname) {
        this.proname = proname;
    }

    public SupervisorProgram() {
    }

    public SupervisorProgram(Long serviceId, String proname) {
        this.serviceId = serviceId;
        this.proname = proname;
    }
}
