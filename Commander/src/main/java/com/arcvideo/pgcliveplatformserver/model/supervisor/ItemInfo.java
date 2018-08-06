package com.arcvideo.pgcliveplatformserver.model.supervisor;

/**
 * Created by zfl on 2018/6/8.
 */
public class ItemInfo {
    private Integer posIdx;
    private Long screenId;
    private Long contentId;
    private Integer outputType;

    public Integer getPosIdx() {
        return posIdx;
    }

    public void setPosIdx(Integer posIdx) {
        this.posIdx = posIdx;
    }

    public Long getScreenId() {
        return screenId;
    }

    public void setScreenId(Long screenId) {
        this.screenId = screenId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Integer getOutputType() {
        return outputType;
    }

    public void setOutputType(Integer outputType) {
        this.outputType = outputType;
    }
}
