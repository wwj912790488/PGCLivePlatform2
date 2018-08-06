package com.arcvideo.pgcliveplatformserver.service.role;

import com.arcvideo.pgcliveplatformserver.entity.URole;
import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import jdk.nashorn.internal.ir.LiteralNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Locale;

/**
 * @author yxu
 */
public interface RoleService {

    URole findOne(Long roleId);

    Page<URole> listRole(Specification<URole> specification, PageRequest pageRequest);

    List<URole> listRole(Specification<URole> specification);

    List<URole> findAll();

    List<URole> findList(List<Long> roleIdList);

    boolean isExistRole(URole role);

    URole addURole(URole uRole);

    ResultBean removeRole(Long roleId);

    boolean verifyTenant(String companyId, List<Long> list);

    URole getRole(RoleType roleType);

    URole addRoleByRoleType(User creator, RoleType roleType, List<RoleType> roleTypeList);

    URole addRoleByRoleType(RoleType roleType,List<RoleType> roleTypeList);

    List<URole> findByUserId(String userId);

    RoleType getHighestRoleByUserId(String userId);

    void addAdminRole();

    List<URole> findByCompanyId(String companyId);
}
