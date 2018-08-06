package com.arcvideo.pgcliveplatformserver.service.user.impl;

import com.arcvideo.pgcliveplatformserver.entity.UTenants;
import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.repo.TenantsRepo;
import com.arcvideo.pgcliveplatformserver.service.user.TenantsService;
import com.arcvideo.pgcliveplatformserver.service.user.UserService;
import com.arcvideo.pgcliveplatformserver.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;

@Service
public class TenantsServiceimpl implements TenantsService {
    @Autowired
    private TenantsRepo tenantsRepo;
    @Autowired
    private UserService userService;
    @Autowired
    MessageSource messageSource;

    @Override
    public Page<UTenants> list(Specification<UTenants> specification, PageRequest pageRequest) {
        return tenantsRepo.findAll(specification,pageRequest);
    }

    @Override
    public Map<String, UTenants> findForMap() {
        Map<String,UTenants> map  = new HashMap<>();
        List<UTenants> list = tenantsRepo.findAll();
        for (UTenants tenants : list ) {
            map.put(tenants.getCompanyId(),tenants);
        }
        return map;
    }

    @Override
    public UTenants saveTenant(UTenants uTenant) {
        /**UTenants parent = tenantsRepo.findOne(uTenant.getParentId());
        uTenant.setParentName(parent.getCompanyName()); //设置上级组织名称 **/
        if (uTenant.getCompanyId() == null || uTenant.getCompanyId().length() <= 0 ) uTenant.setCompanyId(UuidUtil.getUuid());
        uTenant.setCreateTime(new Timestamp(new Date().getTime()));
        return tenantsRepo.save(uTenant);
    }

    @Override
    public boolean saveTenants(List<UTenants> list) {
        list = tenantsRepo.save(list);
        if (list.size() > 0) return true;
        return false;
    }

    @Transactional
    @Override
    public UTenants updateTenant(UTenants uTenant) {
        UTenants update = tenantsRepo.findOne(uTenant.getId());

        if (!update.getCompanyName().equals(uTenant.getCompanyName())) {
            userService.updateCompanyNameByCompanyId(uTenant.getCompanyName(),update.getCompanyId());//当组织名称更新时，同步更新该公司下所有用户
        }

        update.setCompanyName(uTenant.getCompanyName());
        update.setRemarks(uTenant.getRemarks());
        return tenantsRepo.save(update);
    }

    @Override
    public List<UTenants> findByUser(User user) {
        if (user.getRoleType().equals(RoleType.Adminstrator)) {
            return tenantsRepo.findAll();
        }else {
            UTenants tenants = tenantsRepo.findOneByCompanyId(user.getCompanyId());
            List<UTenants> list = new ArrayList<>();
            list.add(tenants);
            return list;
        }
    }

    @Override
    public UTenants findOneByCompanyId(String companyId) {
        return tenantsRepo.findOneByCompanyId(companyId);
    }

    @Override
    public UTenants findOneById(Long id) {
        return tenantsRepo.findOne(id);
    }

    @Override
    public int removeTenant(Set<Long> ids) {
        return tenantsRepo.deleteByIdIn(ids);
    }

    @Override
    public ResultBean verifyDelete(Set<Long> ids) {
        Locale locale = LocaleContextHolder.getLocale();
        List<UTenants> tenantsList = tenantsRepo.findAll(ids);
        Set<String> companyIds = new HashSet<>();
        tenantsList.forEach(uTenants -> {
            companyIds.add(uTenants.getCompanyId());
        });
        //判断租户下是否有用户
        List<User> userList = userService.findByCompanyId(companyIds);
        if (userList.size() > 0) return new ResultBean<>(ResultBean.FAIL,messageSource.getMessage(CodeStatus.TENANT_HAS_USER.getMessage(),null,locale));
        return new ResultBean<>(ResultBean.SUCCESS,"删除成功");
    }

    @Override
    public int countByCompanyName(String companyName) {
        return tenantsRepo.countByCompanyName(companyName);
    }
}
