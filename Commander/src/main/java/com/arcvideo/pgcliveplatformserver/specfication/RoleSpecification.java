package com.arcvideo.pgcliveplatformserver.specfication;

import com.arcvideo.pgcliveplatformserver.entity.URole;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author yxu
 * 角色查询规格
 */
public class RoleSpecification {
    public static Specification<URole> searchKeyword(final String key,RoleType roleType,final String companyId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotEmpty(key)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("roleName"), "%" + key + "%"),
                        criteriaBuilder.like(root.get("createUserName"), "%" + key + "%"),
                        criteriaBuilder.like(root.get("remarks"), "%" + key + "%"),
                        criteriaBuilder.like(root.get("companyName"), "%" + key + "%")));
            }
            List<RoleType> roleTypeList = new ArrayList<>();
            if (roleType.equals(RoleType.Adminstrator)) {
               roleTypeList = Arrays.asList(new RoleType[]{RoleType.User,RoleType.Admin});
            }else {
                roleTypeList = Arrays.asList(new RoleType[]{RoleType.User});
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("companyId"),companyId)));
            }
            predicates.add(criteriaBuilder.and(root.get("roleType").in(roleTypeList)));
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }

    public static Specification<URole> queryAnbleRole(RoleType roleType,final String companyId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<RoleType> roleTypeList = new ArrayList<>();
            if (roleType.equals(RoleType.Adminstrator)) {
                roleTypeList = Arrays.asList(new RoleType[]{RoleType.User,RoleType.Admin});
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.isNull(root.get("companyId"))
                ));
            }else {
                roleTypeList = Arrays.asList(new RoleType[]{RoleType.User});
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("companyId"),companyId)));
            }
            predicates.add(criteriaBuilder.and(root.get("roleType").in(roleTypeList)));
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }
}
