package com.arcvideo.pgcliveplatformserver.specfication;

import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserSpecification {
    public static Specification<User> searchKeyword(final Long id,final RoleType roleType,final String companyId, final String key, final Collection<RoleType> roleTypeCollection) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (StringUtils.isNotEmpty(key)) {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(root.<String>get("name"), "%" + key + "%"),
                            criteriaBuilder.like(root.<String>get("realName"), "%" + key + "%"),
                            criteriaBuilder.like(root.<String>get("partName"),"%" +  key + "%"),
                            criteriaBuilder.like(root.<String>get("remarks"), "%" + key + "%")));
                }
                if (roleTypeCollection.size() > 0) {
                    predicates.add(criteriaBuilder.or(root.<Integer>get("roleType").in(roleTypeCollection.toArray())));
                }
                if (roleType.equals(RoleType.Admin)) {
                    predicates.add(criteriaBuilder.and(
                            criteriaBuilder.equal(root.get("companyId"),companyId)));
                }else if (roleType.equals(RoleType.User)) {
                    predicates.add(criteriaBuilder.and(
                            criteriaBuilder.equal(root.get("id"),id)));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
    }
}
