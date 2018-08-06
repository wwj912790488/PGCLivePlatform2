package com.arcvideo.pgcliveplatformserver.model.supervisor;

/**
 * Created by zfl on 2018/7/6.
 */
public class ProcessResponse {
    private Integer code;
    private SupervisorTaskInfo task;

    public SupervisorTaskInfo getTask() {
        return task;
    }

    public void setTask(SupervisorTaskInfo task) {
        this.task = task;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
