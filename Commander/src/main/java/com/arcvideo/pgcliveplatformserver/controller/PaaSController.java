package com.arcvideo.pgcliveplatformserver.controller;

import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.repo.UserRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * Created by slw on 2018/4/26.
 */
@Controller
@RequestMapping("paas")
public class PaaSController {

    @Autowired
    private UserRepo userRepo;

    @RequestMapping("login")
    public String login(String userName, HttpSession session) {
        if (StringUtils.isNotBlank(userName)) {
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication auth = new UsernamePasswordAuthenticationToken(userName, null, AuthorityUtils.createAuthorityList(RoleType.User.name()));
            context.setAuthentication(auth);
            session.setAttribute("SPRING_SECURITY_CONTEXT", context);
            return "redirect:/content";
        }
        return "redirect:/login";
    }
}
