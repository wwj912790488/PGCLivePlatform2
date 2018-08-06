package com.arcvideo.pgcliveplatformserver.controller;

import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.mediainfo.MediaInfo;
import com.arcvideo.pgcliveplatformserver.service.mediainfo.MediaInfoService;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Created by slw on 2018/3/30.
 */
@Controller
@RequestMapping("api")
public class MediaInfoController {
    private static final Logger logger = LoggerFactory.getLogger(MediaInfoController.class);

    @Autowired
    MediaInfoService mediaInfoService;

    @RequestMapping("getmediainfo")
    @ResponseBody
    public ResultBean<MediaInfo> getMediaInfo(@RequestParam(value = "path", required = true) String path) {
        MediaInfo mediaInfo = mediaInfoService.getMediaInfo(path);
        ResultBean resultBean = new ResultBean(mediaInfo);

        if (mediaInfo == null) {
            resultBean.setCode(ResultBean.FAIL);
            resultBean.setMessage("mediainfo is empty");
        }
        return resultBean;
    }

    @RequestMapping("getVideoInfo")
    @ResponseBody
    public ResultBean<String> getVideoInfo(@RequestParam(value = "path", required = true) String path) {
        MediaInfo mediaInfo = mediaInfoService.getMediaInfo(path);
        String videoInfo = mediaInfoService.getVideoInfo(mediaInfo);
        ResultBean resultBean = new ResultBean(videoInfo);
        if (StringUtils.isBlank(videoInfo)) {
            resultBean.setCode(ResultBean.FAIL);
            resultBean.setMessage("videoinfo is empty");
        }
        return resultBean;
    }

    @RequestMapping("getLiveTaskThumb")
    public void getLiveTaskThumb(Long taskId, @RequestParam(required = false,defaultValue = "") String width, HttpServletResponse response) {
        try {
            byte[] bytes = mediaInfoService.getLiveTaskThumb(taskId);
            if (bytes != null) {
                IOUtils.write(bytes, response.getOutputStream());
            }
        } catch (IOException e) {
            logger.error("read image error:" + e);
        }
    }
}
