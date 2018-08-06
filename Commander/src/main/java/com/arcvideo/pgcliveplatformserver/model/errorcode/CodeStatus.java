package com.arcvideo.pgcliveplatformserver.model.errorcode;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by slw on 2018/7/6.
 */
public enum CodeStatus {
    CONTENT_ERROR_ID_EXISTS(2000001, "content.error.ID_exists"),
    CONTENT_ERROR_NAME_EMPTY(2000002, "content.error.name_empty"),
    CONTENT_ERROR_MASTER_IS_NULL(2000003, "content.error.master_is_null"),
    CONTENT_ERROR_SLAVE_IS_NULL(2000004, "content.error.slave_is_null"),
    CONTENT_ERROR_SOURCE_URI_EMPTY(2000005, "content.error.source_uri_empty"),
    CONTENT_ERROR_PROGRAM_ID_IS_NULL(2000006, "content.error.udp_program_id_is_null"),
    CONTENT_ERROR_AUDIO_ID_IS_NULL(2000007, "content.error.udp_audio_id_is_null"),
    CONTENT_ERROR_DELAY_DURATION_VALUE(2000008, "content.error.delay_duration_value"),
    CONTENT_ERROR_BACKUP_IS_NULL(2000009, "content.error.backup_is_null"),
    CONTENT_ERROR_OUTPUT_URI_EMPTY(2000010, "content.error.output_uri_empty"),
    CONTENT_ERROR_OUTPUT_URI_PARSE(2000011, "content.error.output_uri_parse"),
    CONTENT_ERROR_OUTPUT_TEMPLATE_ID_NOT_FOUND(2000012, "content.error.output_template_id_not_found"),
    CONTENT_ERROR_ID_NOT_FOUND(2000013, "content.error.ID_not_found"),
    CONTENT_ERROR_RANDOM_UDP_EMPTY(2000014, "content.error.random_udp_empty"),
    CONTENT_ERROR_URI_REGEX_RTMP(2000015, "content.error.uri_regex_rtmp_error"),
    CONTENT_ERROR_URI_REGEX_HLS(2000016, "content.error.uri_regex_hls_error"),
    CONTENT_ERROR_URI_REGEX_UDP(2000017, "content.error.uri_regex_udp_error"),

    CONTENT_ERROR_RETRY_TASK_START(2000100, "content.error.retry_task_start"),
    CONTENT_ERROR_RETRY_TASK_STOP(2000101, "content.error.retry_task_stop"),

    CONVENE_ERROR_TASK_CREATE(2100001, "convene.error.task_create"),
    CONVENE_ERROR_TASK_UPDATE(2100002, "convene.error.task_update"),
    CONVENE_ERROR_TASK_DELETE(2100003, "convene.error.task_delete"),
    CONVENE_ERROR_TASK_START(2100004, "convene.error.task_start"),
    CONVENE_ERROR_TASK_STOP(2100005, "convene.error.task_stop"),
    CONVENE_ERROR_SERVER_NOT_AVAILABLE(2100006, "convene.error.server_not_available"),
    CONVENE_ERROR_SERVER_AVAILABLE(2100007, "convene.error.server_available"),

    DELAYER_ERROR_TASK_CREATE(2200001, "delayer.error.task_create"),
    DELAYER_ERROR_TASK_UPDATE(2200002, "delayer.error.task_update"),
    DELAYER_ERROR_TASK_DELETE(2200003, "delayer.error.task_delete"),
    DELAYER_ERROR_TASK_START(2200004, "delayer.error.task_start"),
    DELAYER_ERROR_TASK_STOP(2200005, "delayer.error.task_stop"),
    DELAYER_ERROR_SERVER_NOT_AVAILABLE(2200006, "delayer.error.server_not_available"),
    DELAYER_ERROR_SERVER_AVAILABLE(2200007, "delayer.error.server_available"),

    IPSWITCH_ERROR_TASK_CREATE(2300001, "ipswitch.error.task_create"),
    IPSWITCH_ERROR_TASK_UPDATE(2300002, "ipswitch.error.task_update"),
    IPSWITCH_ERROR_TASK_DELETE(2300003, "ipswitch.error.task_delete"),
    IPSWITCH_ERROR_TASK_START(2300004, "ipswitch.error.task_start"),
    IPSWITCH_ERROR_TASK_STOP(2300005, "ipswitch.error.task_stop"),
    IPSWITCH_ERROR_TASK_SWITCH(2300006, "ipswitch.error.task_switch"),
    IPSWITCH_ERROR_SERVER_NOT_AVAILABLE(2300007, "ipswitch.error.server_not_available"),
    IPSWITCH_ERROR_SERVER_AVAILABLE(2300008, "ipswitch.error.server_available"),

    LIVE_ERROR_TASK_CREATE(2400001, "live.error.task_create"),
    LIVE_ERROR_TASK_UPDATE(2400002, "live.error.task_update"),
    LIVE_ERROR_TASK_DELETE(2400003, "live.error.task_delete"),
    LIVE_ERROR_TASK_START(2400004, "live.error.task_start"),
    LIVE_ERROR_TASK_STOP(2400005, "live.error.task_stop"),
    LIVE_ERROR_SERVER_NOT_AVAILABLE(2400006, "live.error.server_not_available"),
    LIVE_ERROR_SERVER_AVAILABLE(2400007, "live.error.server_available"),

    SUPERVISOR_ERROR_TASK_START(2500001, "supervisor.error.task_start"),
    SUPERVISOR_ERROR_TASK_STOP(2500002, "supervisor.error.task_stop"),
    SUPERVISOR_ERROR_SERVER_NOT_AVAILABLE(2500003, "supervisor.error.server_not_available"),
    SUPERVISOR_ERROR_SERVER_AVAILABLE(2500004, "supervisor.error.server_available"),

    RECORDER_ERROR_TASK_START(2600001, "recorder.error.task_start"),
    RECORDER_ERROR_TASK_STOP(2600002, "recorder.error.task_stop"),
    RECORDER_ERROR_SERVER_NOT_AVAILABLE(2600003, "recorder.error.server_not_available"),
    RECORDER_ERROR_SERVER_AVAILABLE(2600004, "recorder.error.server_available"),

    UDP_RANGE_ERROR_IP_BEGIN(2700001, "udp.range.error.ip_begin"),
    UDP_RANGE_ERROR_IP_END(2700002, "udp.range.error.ip_end"),
    UDP_RANGE_ERROR_IP_RANGE(2700003, "udp.range.error.ip_range"),
    UDP_RANGE_ERROR_PORT_BEGIN(2700004, "udp.range.error.port_begin"),
    UDP_RANGE_ERROR_PORT_END(2700005, "udp.range.error.port_end"),
    UDP_RANGE_ERROR_PORT_RANGE(2700006, "udp.range.error.port_range"),

    VLAN_SETTING_ERROR_NAME_EMPTY(2800001, "vlan.setting.error.name_empty"),
    VLAN_SETTING_ERROR_NAME_EXISTS(2800002, "vlan.setting.error.name_exists"),
    VLAN_SETTING_ERROR_CIDR_EMPTY(2800003, "vlan.setting.error.cidr_empty"),
    VLAN_SETTING_ERROR_CIDR_EXISTS(2800004, "vlan.setting.error.cidr_exists"),
    VLAN_SETTING_ERROR_NIOTYPE_EMPTY(2800005, "vlan.setting.error.niotype_empty"),
    VLAN_SETTING_ERROR_NIOTYPE_NOT_FOUND(2800006, "vlan.setting.error.niotype_not_found"),
    VLAN_SETTING_ERROR_NIOTYPE_EXISTS(2800007, "vlan.setting.error.niotype_exists"),

    ROLE_NOT_REMOVE(2900001,"role.not.remove"),
    ROLE_ROLENAME_EMPTY(2900002,"role.roleName.empty"),
    ROLE_ROLENAME_EXIST(2900003,"role.roleName.exist"),
    ROLE_PRESET(2900004,"role.preset"),

    TENANT_HAS_USER(3000004,"tenant.has.user"),
    TENANT_COMPANYNAME_EMPTY(3000005,"tenant.companyName.empty"),

    USER_COMPANYID_EMPTY(3100001,"user.companyId.empty"),
    USER_NAME_EMPTY(31800002,"user.name.empty"),
    USER_PASSWORD_EMPTY(3100003,"user.password.empty"),
    USER_REPASSWORD_EMPTY(3100004,"user.confirmPassword.empty"),
    USER_PASSWORD_NOTEQUALS(3100005,"user.password.notEquals"),
    USER_PASSWORD_PERM_NOT_UPDATE(3100006,"user.password.perm.not.update"),
    USER_PASSWORD_PERM_NOT_DISABLE(3100007,"user.password.perm.not.disable")
    ;

    private final int code;
    private final String message;

    CodeStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static CodeStatus fromName(String name) {
        CodeStatus codeStatus = null;
        if (StringUtils.isNotEmpty(name)) {
            try {
                codeStatus = CodeStatus.valueOf(name);
            } catch (Exception e) {
            }
        }
        return codeStatus;
    }
public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
