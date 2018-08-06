package com.arcvideo.pgcliveplatformserver.model.dashboard;

/**
 * Created by zfl on 2018/5/23.
 */
public class DashboardInfo {

    private ConnectInfo connectInfo;
    private ContentInfo contentInfo;
    private DecodeInfo decodeInfo;
    private LiveInfo liveInfo;
    private RecordInfo recordInfo;
    private AlertInfo alertInfo;

    public ConnectInfo getConnectInfo() {
        return connectInfo;
    }

    public void setConnectInfo(ConnectInfo connectInfo) {
        this.connectInfo = connectInfo;
    }

    public ContentInfo getContentInfo() {
        return contentInfo;
    }

    public void setContentInfo(ContentInfo contentInfo) {
        this.contentInfo = contentInfo;
    }

    public DecodeInfo getDecodeInfo() {
        return decodeInfo;
    }

    public void setDecodeInfo(DecodeInfo decodeInfo) {
        this.decodeInfo = decodeInfo;
    }

    public LiveInfo getLiveInfo() {
        return liveInfo;
    }

    public void setLiveInfo(LiveInfo liveInfo) {
        this.liveInfo = liveInfo;
    }

    public RecordInfo getRecordInfo() {
        return recordInfo;
    }

    public void setRecordInfo(RecordInfo recordInfo) {
        this.recordInfo = recordInfo;
    }

    public AlertInfo getAlertInfo() {
        return alertInfo;
    }

    public void setAlertInfo(AlertInfo alertInfo) {
        this.alertInfo = alertInfo;
    }
}
