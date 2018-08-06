package com.arcvideo.pgcliveplatformserver.controller;

import com.arcvideo.pgcliveplatformserver.service.setting.SettingService;
import com.arcvideo.pgcliveplatformserver.util.file.ContentFileFilter;
import com.arcvideo.pgcliveplatformserver.util.file.ContentFileFilterImpl;
import com.arcvideo.pgcliveplatformserver.util.file.FolderUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

@Controller
@RequestMapping("/path")
public class PathSelectorController {
    @Autowired
    private SettingService settingService;

    private ContentFileFilter contentFileFilter = null;

    @PostConstruct
    private void init() {
        contentFileFilter = new ContentFileFilterImpl(settingService.getDefaultFileNameExtension(), null, null, null, null, null);
    }

    @RequestMapping(value = "/querycontentfolderlist", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public List queryContentFolderList(@RequestParam(value = "foldermode") Boolean folderMode, @RequestParam(value = "id") String id, @RequestParam(value = "path", required = false) String path) {
        File defaultRecorder = FileUtils.getFile(settingService.getRecorderFileDir());
        if (!defaultRecorder.exists()) {
            defaultRecorder.mkdirs();
        }
        String defaultRootFolderName = defaultRecorder.getAbsolutePath();
        if(StringUtils.isEmpty(path)){
            return FolderUtil.queryFolderAndFileList(folderMode, id, defaultRootFolderName, contentFileFilter);
        }else{
            return FolderUtil.queryFirstFolderAndFileList(folderMode, id, defaultRootFolderName, contentFileFilter, path);
        }
    }

    @RequestMapping(value = "/createfolder", method = RequestMethod.POST)
    @ResponseBody
    public String createFolder(@RequestParam(value = "path") String path, @RequestParam(value = "name") String name) {
        File file = new File(path, name);
        if (!file.mkdir()) {
            return "error";
        }
            return "success";
    }

}
