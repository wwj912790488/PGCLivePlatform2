package com.arcvideo.pgcliveplatformserver.model;

public class TaskResult {

    private String taskId;
    private TaskStatus state;
    private String taskName;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public TaskStatus getState() {
        return state;
    }

    public void setState(TaskStatus state) {
        this.state = state;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

}
