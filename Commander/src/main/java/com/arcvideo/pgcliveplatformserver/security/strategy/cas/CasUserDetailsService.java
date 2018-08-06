
package com.arcvideo.pgcliveplatformserver.security.strategy.cas;

import com.alibaba.fastjson.JSON;
import com.arcvideo.pgcliveplatformserver.entity.URole;
import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.security.SecurityUser;
import com.arcvideo.pgcliveplatformserver.service.role.RoleService;
import com.arcvideo.pgcliveplatformserver.service.user.ExternalUserService;
import com.arcvideo.pgcliveplatformserver.service.user.UserService;
import com.arcvideo.pgcliveplatformserver.util.DecodeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用于加载用户信息 实现UserDetailsService接口，或者实现AuthenticationUserDetailsService接口
 * @author
 *
 */
@Service("casUserDetailsService")
public class CasUserDetailsService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {
    Logger logger = LoggerFactory.getLogger(CasUserDetailsService.class);

    @Autowired
    private ExternalUserService externalUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;


    @Override
    public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
        /**********************设置cas用户信息 begin*********************/
        Map<String,Object> attributes = token.getAssertion().getPrincipal().getAttributes();
        logger.info("cas attributes:" + JSON.toJSONString(attributes));
        if (attributes.size() == 0) throw new UsernameNotFoundException("username not found");
        attributes.forEach((k,v) -> {
            attributes.replace(k,DecodeUtils.decodeString(v.toString()));
        });
        String userId = attributes.get("userId").toString();
        logger.info("cas userId:" + userId);
        if (!StringUtils.isNotEmpty(userId)) throw new UsernameNotFoundException("username not found");
        User user = userService.findByUserId(userId);
        if (user == null) {
            externalUserService.syncUser();
            user = userService.findByUserId(userId);
        }
        if (user == null) {
            throw new UsernameNotFoundException("username not found");
        }
        SecurityUser securityUser  = new SecurityUser(user);
        securityUser.setAttributes(attributes);
        user.setName(token.getName());
        /**********************设置cas用户信息 END*********************/

        /**********************设置用户权限 BEGIN*********************/
        List<URole> roleList = roleService.findByUserId(userId);
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

