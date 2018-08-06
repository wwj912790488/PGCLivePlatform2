package com.arcvideo.pgcliveplatformserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by slw on 2018/3/27.
 */
@Controller
@RequestMapping("preview")
public class PreviewController {

    @RequestMapping("")
    public String preview(@RequestParam(required = false) String path, Model model) {
        model.addAttribute("path", path);
        return "preview/preview";
    }
}
