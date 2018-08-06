package com.arcvideo.pgcliveplatformserver.service.ipswitch;

import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.IpSwitchTask;

/**
 * Created by slw on 2018/4/9.
 */
public interface IpSwitchTaskService {
    Boolean startIpSwitch(Content content);
    Boolean stopIpSwitch(Content content);
    Boolean switchingIpSwitch(Long contentId, IpSwitchTask.Type switchType);
}
