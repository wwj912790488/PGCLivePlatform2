package com.arcvideo.pgcliveplatformserver.security.strategy.common;

import com.arcvideo.pgcliveplatformserver.entity.URole;
import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.repo.UserRepo;
import com.arcvideo.pgcliveplatformserver.security.SecurityUser;
import com.arcvideo.pgcliveplatformserver.service.role.RoleService;
import com.arcvideo.pgcliveplatformserver.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    private RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String username){
        User user = userRepo.findByNameIgnoreCaseAndIsDisabled(username,false);
        if (user == null) throw new UsernameNotFoundException("username not found");

        /**********************设置用户信息 begin*********************/
        Map<String,Object> attributes = new HashMap<>();
        attributes.put(UserUtil.LOGIN_ID,user.getName());
        attributes.put(UserUtil.USER_ID,user.getUserId());
        attributes.put(UserUtil.COMPANY_ID,user.getCompanyId());
        SecurityUser securityUser  = new SecurityUser(user);
        securityUser.setAttributes(attributes);
        /**********************设置用户信息 END*********************/

        /**********************设置用户权限 BEGIN*********************/
        List<URole> roleList = roleService.findByUserId(user.getUserId());
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (URole role : roleList) {
            GrantedAuthority grantedAuthority = null;
            /**********下列判断设置对应 CustomFilterInvocationSecurityMetadataSource *****************/
            if (role.getRoleType().equals(RoleType.Adminstrator)) {  //如果登录用户RoleType 是 adminstartor 则使用RoleType.Adminstrator.name() 做为角色名
                grantedAuthority = new SimpleGrantedAuthority(RoleType.Adminstrator.name());
            }else if (role.getRoleType().equals(RoleType.Admin)) { //如果登录用户RoleType 是 Admin 则使用RoleType.Admin.name() 做为角色名
                grantedAuthority = new SimpleGrantedAuthority(RoleType.Admin.name());
            }else { ////如果登录用户RoleType 是 别的 则使用id 做为角色名
                grantedAuthority = new SimpleGrantedAuthority(String.valueOf(role.getId()));
            }
            grantedAuthorities.add(grantedAuthority);
        }
        securityUser.setGrantedAuthorities(grantedAuthorities);
        /**********************设置用户权限 END*********************/
        return securityUser;
    }
}

