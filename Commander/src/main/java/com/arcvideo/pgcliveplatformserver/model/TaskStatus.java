package com.arcvideo.pgcliveplatformserver.model;

public enum TaskStatus {
    tsReady,      // 就绪
    tsExecuting,      // 正在执行任务
    tsComplete,       // 执行完成任务
    tsPause,            // 暂停
    tsFailed,         // 采集失败标记
}
