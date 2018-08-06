package com.arcvideo.pgcliveplatformserver.service.user.impl;

import com.arcvideo.pgcliveplatformserver.entity.URole;
import com.arcvideo.pgcliveplatformserver.entity.UTenants;
import com.arcvideo.pgcliveplatformserver.entity.UUserRole;
import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.model.user.UserResult;
import com.arcvideo.pgcliveplatformserver.model.user.UserSaveCommand;
import com.arcvideo.pgcliveplatformserver.repo.UserRepo;
import com.arcvideo.pgcliveplatformserver.security.WebPasswordEncoder;
import com.arcvideo.pgcliveplatformserver.service.role.RoleService;
import com.arcvideo.pgcliveplatformserver.service.user.TenantsService;
import com.arcvideo.pgcliveplatformserver.service.user.UserRoleService;
import com.arcvideo.pgcliveplatformserver.service.user.UserService;
import com.arcvideo.pgcliveplatformserver.util.UuidUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.loader.custom.sql.SQLCustomQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    RoleService roleService;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    TenantsService tenantsService;

    @PersistenceContext
    EntityManager em;

    @Override
    public User findById(Long id) {
        return userRepo.findOne(id);
    }

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    public Map<String, User> findMapUserAll() {
        List<User> list = this.findAll();
        Map<String,User> map = new HashMap<>();
        for (User user : list) {
            if (StringUtils.isNotEmpty(user.getUserId())) {
                map.put(user.getUserId(),user);
            }
        }
        return map;
    }

    @Transactional
    @Override
    public User createUser(UserSaveCommand userSaveCommand) {
        UTenants tenant = tenantsService.findOneByCompanyId(userSaveCommand.getCompanyId());
        User user = new User();
        BeanUtils.copyProperties(userSaveCommand,user);
        user.setCompanyName(tenant.getCompanyName());
        user.setRoleType(RoleType.User);
        user.setCreateTime(new Timestamp(new Date().getTime()));
        PasswordEncoder webPasswordEncoder = new WebPasswordEncoder();
        user.setPassword(webPasswordEncoder.encode(user.getPassword()));
        if (user.getUserId() == null || user.getUserId().length() <= 0 )
            user.setUserId(UuidUtil.getUuid().toUpperCase());
        user = userRepo.save(user);

        /*//关联角色
        UUserRole uUserRole = new UUserRole();
        uUserRole.setRoleId(userSaveCommand.getRoleId());
        uUserRole.setUserId(user.getUserId());
        userRoleService.addUserRole(uUserRole);*/
        return user;
    }

    @Override
    public User editUser(UserSaveCommand userSaveCommand) {
        User user = this.findById(userSaveCommand.getId());
        if (user.getCompanyId().equals(userSaveCommand.getCompanyId())) {
            UTenants tenant = tenantsService.findOneByCompanyId(userSaveCommand.getCompanyId());
            user.setCompanyName(tenant.getCompanyName());
        }
        user.setRealName(userSaveCommand.getRealName());
        user.setRemarks(userSaveCommand.getRemarks());
        user = userRepo.save(user);

        /*//关联角色
        boolean roleEquals = false;
        List<UUserRole> userRoleList = userRoleService.findByUserId(user.getUserId());//查出用户原来的角色
        for (UUserRole uUserRole : userRoleList) {
            if (uUserRole.getRoleId().longValue() == userSaveCommand.getRoleId().longValue()) roleEquals = true;
        }
        if (!roleEquals) {//如果不存在，则需要更新角色
            userRoleService.deleteAllByUserId(user.getUserId());

            UUserRole uUserRole = new UUserRole();
            uUserRole.setRoleId(userSaveCommand.getRoleId());
            uUserRole.setUserId(user.getUserId());
            userRoleService.addUserRole(uUserRole);
        }*/
        return user;
    }

    @Override
    public List<User> save(List<User> userList) {
        PasswordEncoder webPasswordEncoder = new WebPasswordEncoder();
        for (User user : userList) {
            if (StringUtils.isNotEmpty(user.getPassword())) {
                user.setPassword(webPasswordEncoder.encode(user.getPassword()));
            }
        }
        return userRepo.save(userList);
    }

    @Override
    public void updateRoleType(RoleType roleType,long id) {
        userRepo.updateRoleType(roleType,id);
    }

    @Override
    public void changePassword(Long id, String password) {
        User user = userRepo.findOne(id);
        PasswordEncoder passwordEncoder = new WebPasswordEncoder();
        user.setPassword(passwordEncoder.encode(password));
        userRepo.save(user);
    }

    @Override
    public void settingAdministrator(long id) {
        User user = this.findById(id);
        URole role = roleService.getRole(RoleType.Adminstrator);
        if (role == null) {
            List<RoleType> roleTypeList = Arrays.asList(new RoleType[]{RoleType.Adminstrator,RoleType.Admin,RoleType.User});
            role = roleService.addRoleByRoleType(user,RoleType.Adminstrator,roleTypeList);
        }
        this.updateRoleType(RoleType.Adminstrator,id);

        userRoleService.deleteAllByUserId(user.getUserId());//删除原有的权限

        UUserRole uUserRole = new UUserRole();
        uUserRole.setRoleId(role.getId());
        uUserRole.setUserId(user.getUserId());
        userRoleService.addUserRole(uUserRole);

    }

    @Override
    public ResultBean setTenantAdmin(String sttingUserId,String userId) {
        User user = this.findByUserId(userId);
        //判断用户是否是超级管理员,如果已经是超级管理员,则不能被设置为组织管理员
        if (user.getRoleType().equals(RoleType.Adminstrator)) {
            return new ResultBean(ResultBean.FAIL,"用户已经是超级管理员，不能再加其他权限");
        }
        URole role = roleService.getRole(RoleType.Admin);
        userRoleService.deleteAllByUserId(user.getUserId());//删除原有的权限

        this.updateRoleType(RoleType.Admin,user.getId());
        UUserRole uUserRole = new UUserRole();
        uUserRole.setRoleId(role.getId());
        uUserRole.setUserId(userId);
        userRoleService.addUserRole(uUserRole);

        return new ResultBean(ResultBean.SUCCESS,"授权成功!");
    }

    @Override
    public Page<User> listUser(Specification<User> specification, PageRequest pageRequest) {
        return userRepo.findAll(specification,pageRequest);
    }

    @Override
    public boolean removeUserByUserId(String userId) {
        int row = userRepo.deleteByUserId(userId);
        if (row <= 0) return false;
        return true;
    }

    @Override
    public User findDisabledByName(String name) {
        if (StringUtils.isNotEmpty(name)) {
            return userRepo.findByNameIgnoreCaseAndIsDisabled(name,true);
        }
        return null;
    }

    @Override
    public User findNotDisabledByName(String name) {
        if (StringUtils.isNotEmpty(name)) {
            return userRepo.findByNameIgnoreCaseAndIsDisabled(name,false);
        }
        return null;
    }

    @Override
    public User findByUserId(String userId) {
        return userRepo.findByUserId(userId);
    }

    @Override
    public List<User> findByUserIds(List<String> userIdList, boolean isDisabled) {
        return userRepo.findByUserIdInAndIsDisabled(userIdList,isDisabled);
    }

    @Override
    public Page<UserResult> list(User user,String keyword,PageRequest pageRequest) {
        RoleType highestRoleType = roleService.getHighestRoleByUserId(user.getUserId());//得到查询用户的最高权限
        List<UserResult> userResultList  = new ArrayList<>();
        StringBuffer sql = new StringBuffer("select u.id as id,u.name as name,u.part_id as partId,u.part_name as partName,u.password as password,u.phone ,u.email,").append(" ")
                .append("u.real_name as realName ,u.remarks ,u.role_type as roleType,u.company_name as companyName,u.user_id as userId,group_concat(r.role_name) as roleName,u.company_id as companyId,").append(" ")
                .append("u.create_time as createTime ,u.disable_Time as disableTime,u.is_disabled ").append(" ")
                .append("from user u").append(" ")
                .append("left join u_user_role ur on ur.user_id = u.user_id").append(" ")
                .append("left join u_role r on ur.role_id = r.id GROUP BY u.id").append(" ");
        sql.append("HAVING name != 'admin' and u.is_disabled = 0 ");

        if (StringUtils.isNotEmpty(keyword)) {
            sql.append(" and ( " +
                    "name like '%").append(keyword).append("%'")
                    .append(" or real_name like '%").append(keyword).append("%'")
                    .append(" or part_name like '%").append(keyword).append("%'")
                    .append(" or company_name like '%").append(keyword).append("%'")
                    .append(" or roleName like '%").append(keyword).append("%'")
                    .append(" or remarks like '%").append(keyword).append("%' " +
                    ")");
        }
        if (highestRoleType.equals(RoleType.Adminstrator)) {
            sql.append(" and 1=1");
        } else if (highestRoleType.equals(RoleType.Admin)) {
            sql.append(" and (role_type = ").append(RoleType.Admin.ordinal()).append(" or role_type = ").append(RoleType.User.ordinal()).append(")");
            sql.append(" and company_id ='").append(user.getCompanyId()).append("'");
        }else if (highestRoleType.equals(RoleType.User)) {
            sql.append(" and id=").append(user.getId());
        }
        Query query = em.createNativeQuery(sql.toString());
        query.setFirstResult(pageRequest.getOffset());
        query.setMaxResults(pageRequest.getPageSize());
        userResultList = query.unwrap(SQLQuery.class)
                .addScalar("id",LongType.INSTANCE)
                .addScalar("name")
                .addScalar("partId")
                .addScalar("partName")
                .addScalar("password")
                .addScalar("email")
                .addScalar("phone")
                .addScalar("remarks")
                .addScalar("realName")
                .addScalar("roleType",LongType.INSTANCE)
                .addScalar("companyName")
                .addScalar("userId")
                .addScalar("roleName")
                .addScalar("createTime",TimestampType.INSTANCE)
                .addScalar("disableTime",TimestampType.INSTANCE)
                .addScalar("companyId").setResultTransformer(Transformers.aliasToBean(UserResult.class)).list();
        /*查询总共的数据条目*/
        query = em.createNativeQuery("SELECT COUNT(*) FROM (" + sql.toString() + " ) t");
        Object object = query.getResultList().get(0);
        int totalElements = Integer.parseInt(object.toString());

        Page<UserResult> page = new PageImpl<UserResult>(userResultList,pageRequest,totalElements);
        return page;
    }

    @Override
    public List<User> findByCompanyId(Set<String> companyIds) {
        if (companyIds.size() <= 0) return new ArrayList<>();
        return userRepo.findByCompanyIdInAndIsDisabled(companyIds,false);
    }


    @Override
    public void disable(String userId) {
        userRepo.disable(true,new Timestamp(new Date().getTime()),userId);
    }

    @Override
    public void updateCompanyNameByCompanyId(String companyName,String companyId) {
        userRepo.updateCompanyNameByCompanyId(companyName,companyId);
    }
}
