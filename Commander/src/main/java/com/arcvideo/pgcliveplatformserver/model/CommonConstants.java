package com.arcvideo.pgcliveplatformserver.model;

/**
 * Created by zfl on 2018/6/27.
 */
public class CommonConstants {
    public static final String SUPERVISOR_PROVIDER_TB = "0";
    public static final String SUPERVISOR_PROVIDER_DH = "1";

    public static final String PGC_CONVENE_DEVICE_ENTITY_ID = "pgc_convene_device";
    public static final String PGC_DELAYER_DEVICE_ENTITY_ID = "pgc_delayer_device";
    public static final String PGC_IPSWITCH_DEVICE_ENTITY_ID = "pgc_ipswitch_device";
    public static final String PGC_LIVE_DEVICE_ENTITY_ID = "pgc_live_device";
    public static final String PGC_RECORDER_DEVICE_ENTITY_ID = "pgc_recorder_device";
    public static final String PGC_SUPERVISOR_DEVICE_ENTITY_ID = "pgc_supervisor_device";

    public static final String RegEx_UDP = "^(udp|UDP)://(?:(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))):(6553[0-5]|655[0-2][0-9]|65[0-4][0-9]{2}|6[0-4][0-9]{3}|[1-5][0-9]{4}|[1-9][0-9]{0,3})$";
    public static final String RegEx_HLS = "^(http|HTTP)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]\\.(m|M)3(u|U)8$";
    public static final String RegEx_RTMP = "^(rtmp|RTMP)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]$";

    public static final int STREAM_TYPE_PUSH = 1;
    public static final int STREAM_TYPE_PULL = 2;
    public static final int STREAM_TYPE_UDP = 3;

    public static final int CHANNEL_TYPE_MASTER = 0;
    public static final int CHANNEL_TYPE_SLAVE = 1;
    public static final int CHANNEL_TYPE_BACKUP = 2;
}
