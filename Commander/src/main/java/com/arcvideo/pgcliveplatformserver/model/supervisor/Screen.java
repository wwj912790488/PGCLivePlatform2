package com.arcvideo.pgcliveplatformserver.model.supervisor;

import java.util.List;

/**
 * Created by zfl on 2018/7/4.
 */
public class Screen {
    private Integer output_type;
    private String opsId;
    private String output;
    private List<ScreenTemplate> template;

    public Integer getOutput_type() {
        return output_type;
    }

    public void setOutput_type(Integer output_type) {
        this.output_type = output_type;
    }

    public String getOpsId() {
        return opsId;
    }

    public void setOpsId(String opsId) {
        this.opsId = opsId;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public List<ScreenTemplate> getTemplate() {
        return template;
    }

    public void setTemplate(List<ScreenTemplate> template) {
        this.template = template;
    }
}
