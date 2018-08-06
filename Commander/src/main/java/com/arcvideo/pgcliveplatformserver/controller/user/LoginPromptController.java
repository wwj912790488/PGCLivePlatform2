package com.arcvideo.pgcliveplatformserver.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("user/prompt")
public class LoginPromptController {

    @RequestMapping(value = "")
    public String rolePage() {
        return "user/prompt/prompt";
    }
}
