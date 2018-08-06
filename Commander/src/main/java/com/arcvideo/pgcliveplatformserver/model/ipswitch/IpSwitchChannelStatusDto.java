package com.arcvideo.pgcliveplatformserver.model.ipswitch;

/**
 * Created by slw on 2018/4/16.
 */
public class IpSwitchChannelStatusDto {
    private Long id;
    private Integer isError;
    private Integer isLocked0;
    private Integer isLocked1;
    private Integer isLocked2;
    private Integer currentLocked;
    private Integer isAuto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIsError() {
        return isError;
    }

    public void setIsError(Integer isError) {
        this.isError = isError;
    }

    public Integer getIsLocked0() {
        return isLocked0;
    }

    public void setIsLocked0(Integer isLocked0) {
        this.isLocked0 = isLocked0;
    }

    public Integer getIsLocked1() {
        return isLocked1;
    }

    public void setIsLocked1(Integer isLocked1) {
        this.isLocked1 = isLocked1;
    }

    public Integer getIsLocked2() {
        return isLocked2;
    }

    public void setIsLocked2(Integer isLocked2) {
        this.isLocked2 = isLocked2;
    }

    public Integer getCurrentLocked() {
        return currentLocked;
    }

    public void setCurrentLocked(Integer currentLocked) {
        this.currentLocked = currentLocked;
    }

    public Integer getIsAuto() {
        return isAuto;
    }

    public void setIsAuto(Integer isAuto) {
        this.isAuto = isAuto;
    }
}
