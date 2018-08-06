package com.arcvideo.pgcliveplatformserver.model.supervisor;

import java.util.List;

/**
 * Created by zfl on 2018/4/23.
 */
public class SupervisorPrograms {
    private Long id;
    private List<SupervisorProgram> programList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<SupervisorProgram> getProgramList() {
        return programList;
    }

    public void setProgramList(List<SupervisorProgram> programList) {
        this.programList = programList;
    }
}
