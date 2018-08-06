package com.arcvideo.pgcliveplatformserver.service.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arcvideo.pgcliveplatformserver.entity.UTenants;
import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.model.user.TenantDto;
import com.arcvideo.pgcliveplatformserver.model.user.UserDto;
import com.arcvideo.pgcliveplatformserver.service.role.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 外部用户业务
 * @author yxu
 */
@Service
public class ExternalUserService {
    private static final Logger logger = LoggerFactory.getLogger(ExternalUserService.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private TenantsService tenantsService;

    @Value("${onair.full.request.address}")
    private String fullAddress;

    @Value("${onair.accessKey}")
    private String accessKey;




    /**
     * 请求外部系统用户信息
     * @return
     */
    public List<UserDto> findExternalUsers(String tenantCode) {
        StringBuffer urlBuffer = new StringBuffer(fullAddress).append("/users/all/info?accessKey=").append(accessKey).append("&timeStamp=1&accessToken=1");
        logger.info("user url :" + urlBuffer.toString());
        if (tenantCode != null && tenantCode.length() > 0) {
            urlBuffer.append("&tenantCode=").append(tenantCode);
        }
        JSONObject s = restTemplate.postForObject(urlBuffer.toString(),null,JSONObject.class);
        logger.info("user request info:" + JSON.toJSONString(s));
        if (s == null) return new ArrayList<>();
        JSONObject data = s.getJSONObject("data");
        if (data == null) return new ArrayList<>();
        JSONArray jsonArray = data.getJSONArray("users");
        if (jsonArray == null || jsonArray.size() <=0) return new ArrayList<>();
        List<UserDto> list = jsonArray.toJavaList(UserDto.class);
        return list;
    }

    public List<TenantDto> findExternalTenants() {
        StringBuffer urlBuffer = new StringBuffer(fullAddress).append("/users/getAllCompanyInfo?accessKey=").append(accessKey).append("&timeStamp=1&accessToken=1");
        logger.info("tenant url :" + urlBuffer.toString());
        JSONObject s = restTemplate.postForObject(urlBuffer.toString(),null,JSONObject.class);
        logger.info("tenant request info:" + JSON.toJSONString(s));
        if (s == null) return new ArrayList<>();
        JSONObject data = s.getJSONObject("data");
        if (data == null) return new ArrayList<>();
        JSONArray jsonArray = data.getJSONArray("companyList");
        if (jsonArray == null || jsonArray.size() <=0) return new ArrayList<>();
        List<TenantDto> list = jsonArray.toJavaList(TenantDto.class);

        return list;
    }


    /**
     * 同步租户和用户
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void syncUser() {
        List<User> allUser = new ArrayList<>();
        List<TenantDto> tenantDtoList = findExternalTenants();
        for (TenantDto tenantDto : tenantDtoList) {
            List<UserDto> userDtoList = findExternalUsers(tenantDto.getTenantCode());//用户租户code去拉取用户信息
            List<User> aa = ConvertUser(userDtoList,tenantDto.getCompanyId(),tenantDto.getCompanyName());//用companyId去关联数据
            allUser.addAll(aa);
        }

        List<UTenants> tenantList = this.convertTenant(tenantDtoList);
        //同步租户信息
        tenantsService.saveTenants(tenantList);
        //同步用户信息
        userService.save(allUser);
        //预生成租户管理员角色
        roleService.addAdminRole();

    }

    public List<UTenants> convertTenant(List<TenantDto> tenantDtoList) {
        Map<String,UTenants> tenantsMap = tenantsService.findForMap();
        List<UTenants> tenantsList = new ArrayList<>();
        for (TenantDto tenantDto : tenantDtoList) {
            UTenants tenant = new UTenants();
            BeanUtils.copyProperties(tenantDto,tenant);
            if (!tenant.equals(tenantsMap.get(tenant.getCompanyId()))) {
                tenantsList.add(tenant);
            }
        }
        return tenantsList;
    }

    /**
     *  将外部系统用户转换成内容用户
     * @param userDtoList
     * @return
     */
    public List<User> ConvertUser(List<UserDto> userDtoList,String companyId,String companyName) {
        Map<String,User> originMap  = userService.findMapUserAll();
        List<User> userList = new ArrayList<>();
        for (UserDto userDto : userDtoList) {
            if (userDto.getUserStatus() == 1) {continue;}//如果用户为停用状态则跳过.
            User originUser = originMap.get(userDto.getUserId());
            if (originUser == null) {//源数据表中该用户为空,则为新用户

                User user = new User();
                BeanUtils.copyProperties(userDto,user);
                user.setRealName(userDto.getUserName());
                user.setName(userDto.getLoginId());
                user.setEmail(userDto.getEmail());
                user.setPassword("1");
                user.setRoleType(RoleType.User);
                user.setCompanyId(companyId);
                user.setCompanyName(companyName);
                userList.add(user);
                continue;
            }


            UserDto tempUserDto  =new UserDto(originUser.getName(),originUser.getRealName(),originUser.getPartId(),originUser.getPartName(),originUser.getEmail(),originUser.getPhone());
            if (tempUserDto.equals(userDto)) continue; //如果这些数据全相等则,不同步
            userService.removeUserByUserId(originUser.getUserId());
            User user = new User();
            BeanUtils.copyProperties(userDto,user);
            user.setRealName(userDto.getUserName());
            user.setName(userDto.getLoginId());
            user.setEmail(userDto.getEmail());
            user.setPassword("1");
            user.setRoleType(originUser.getRoleType());
            user.setCompanyId(companyId);
            userList.add(user);
        }
        return userList;
    }

}
