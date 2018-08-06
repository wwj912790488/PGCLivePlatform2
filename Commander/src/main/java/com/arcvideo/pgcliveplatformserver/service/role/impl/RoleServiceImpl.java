package com.arcvideo.pgcliveplatformserver.service.role.impl;

import com.arcvideo.pgcliveplatformserver.entity.UMenu;
import com.arcvideo.pgcliveplatformserver.entity.URole;
import com.arcvideo.pgcliveplatformserver.entity.UUserRole;
import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.repo.RoleRepo;
import com.arcvideo.pgcliveplatformserver.service.menu.MenuService;
import com.arcvideo.pgcliveplatformserver.service.role.RoleService;
import com.arcvideo.pgcliveplatformserver.service.user.UserRoleService;
import com.arcvideo.pgcliveplatformserver.service.user.UserService;
import org.apache.poi.hssf.util.HSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private MenuService menuService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserService userService;
    @Autowired
    MessageSource messageSource;


    @Override
    public URole findOne(Long roleId) {
        return roleRepo.findOne(roleId);
    }

    @Override
    public Page<URole> listRole(Specification<URole> specification,PageRequest pageRequest) {
        return roleRepo.findAll(specification,pageRequest);
    }

    @Override
    public List<URole> listRole(Specification<URole> specification) {
        return roleRepo.findAll(specification);
    }

    @Override
    public List<URole> findAll() {
        return roleRepo.findAll();
    }

    @Override
    public List<URole> findList(List<Long> roleIdList) {
        return roleRepo.findAll(roleIdList);
    }

    @Override
    public boolean isExistRole(URole role) {
        User user = userService.findById(role.getCreateUserId());
        //判断分为新增和修改，其中这两种情况，又分成超管和其他角色操作两种情况
        URole origin = null;
        if (user.getRoleType().equals(RoleType.Adminstrator) && user.getCompanyId() == null) {
            origin = roleRepo.getByRoleName(role.getRoleName());
        }else {
            origin = roleRepo.getByRoleNameAndCompanyId(role.getRoleName().trim(),role.getCompanyId());
        }
        if (origin == null) { //数据库中 不存在 名称的角色
            return false;
        }else {               //数据库中 存在 该名称的角色
            if (role.getId() == null) {  //参数roleId为空，说明为新增,新增操作下，如果角色名已存在则不允许
                return true;
            }else {
                if (role.getId().longValue() != origin.getId().longValue()) { //判断根据角色名查询出的数据ID是否和修改的角色ID相同，如果不同则说明角色名被修改，则数据库已经存在。
                    return true;
                }else {
                    return false;
                }
            }
        }
    }

    @Override
    public URole addURole(URole uRole) {
        if (uRole.getCreateTime() == null) uRole.setCreateTime(new Timestamp(new Date().getTime()));
        return roleRepo.save(uRole);
    }

    @Override
    public ResultBean removeRole(Long roleId) {
        Locale locale = LocaleContextHolder.getLocale();
        ResultBean rb = new ResultBean();
        URole role = findOne(roleId);
        if (role == null) {
            rb.setCode(ResultBean.FAIL);
            rb.setMessage("角色不存在,请刷新确认");
            return rb;
        }
        if (role.getRoleType().equals(RoleType.Adminstrator) || role.getRoleType().equals(RoleType.Admin)) {
            rb.setCode(ResultBean.FAIL);
            rb.setMessage(messageSource.getMessage(CodeStatus.ROLE_NOT_REMOVE.getMessage(),null,locale));
            return rb;
        }
        List<UUserRole> userRoleList = userRoleService.getRoleByRoleId(roleId);
        List<String> userIdList = new ArrayList<>();
        for (UUserRole uUserRole : userRoleList) userIdList.add(uUserRole.getUserId());
        List<User> userList = userService.findByUserIds(userIdList,false);
        if (userList != null && userList.size() > 0) {
            rb.setCode(ResultBean.FAIL);
            rb.setMessage("该角色已经被使用，不能被删除!");
            return rb;
        }
        roleRepo.delete(roleId);
        rb.setCode(ResultBean.SUCCESS);
        rb.setMessage("删除角色成功");
        return rb;
    }

    @Override
    public boolean verifyTenant(String companyId, List<Long> list) {
        List<URole> roleList = this.findList(list);
        boolean bool = true;
       for (URole role : roleList) {
           if (!companyId.equals(role.getCompanyId())) {
               if (role.getRoleType().equals(RoleType.User)) {
                   bool = false;
               }
           }
       }
       return bool;
    }

    @Override
    public URole getRole(RoleType roleType) {
        return roleRepo.findByRoleType(roleType);
    }

    @Override
    public URole addRoleByRoleType(User creator,RoleType roleType,List<RoleType> roleTypeList) {
        URole role = new URole();
        if (creator != null) {
            role.setCreateUserName(creator.getRealName());
            role.setCreateUserId(creator.getId());
        }
        role.setRoleName(roleType.getMessageKey());
        role.setRoleType(roleType);
        List<UMenu> menuList = menuService.findByRoleTypeColl(roleTypeList);
        StringBuffer buffer = new StringBuffer();
        menuList.forEach((v)-> {
            buffer.append(v.getId()).append(",");
        });
        role.setMenuIds(buffer.toString());
        role.setCreateTime(new Timestamp(new Date().getTime()));
        role = this.addURole(role);
        return role;
    }

    @Override
    public URole addRoleByRoleType(RoleType roleType,List<RoleType> roleTypeList) {
        return this.addRoleByRoleType(null,roleType,roleTypeList);
    }

    @Override
    public List<URole> findByUserId(String userId) {
        List<UUserRole> userRoleList = userRoleService.findByUserId(userId);
        List<Long> roleIdList = userRoleList.stream().map(userRole ->userRole.getRoleId()).collect(Collectors.toList());
        List<URole> roleList = roleRepo.findAll(roleIdList);
        return roleList;
    }

    @Override
    public RoleType getHighestRoleByUserId(String userId) {
        RoleType roleType = null;
        List<URole> roleList = this.findByUserId(userId);
        for (URole role : roleList) {
            if (roleType == null) {
                roleType = role.getRoleType();
            }else if (roleType.ordinal() > role.getRoleType().ordinal()){
                roleType = role.getRoleType();
            }
        }
        return roleType;
    }

    @Override
    public void addAdminRole() {
        URole role = this.getRole(RoleType.Admin);
        if (role == null) {
            List<RoleType> roleTypeList = Arrays.asList(new RoleType[]{RoleType.Admin,RoleType.User});
            this.addRoleByRoleType(RoleType.Admin,roleTypeList);
        }
    }

    @Override
    public List<URole> findByCompanyId(String companyId) {
        return roleRepo.findByCompanyId(companyId);
    }
}
