package com.arcvideo.pgcliveplatformserver.specfication;

import com.arcvideo.pgcliveplatformserver.entity.UTenants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class TenantSpecification {
    public static Specification<UTenants> searchKeyword(final String key) {
        return new Specification<UTenants>() {
            @Override
            public Predicate toPredicate(Root<UTenants> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (StringUtils.isNotEmpty(key)) {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(root.<String>get("companyName"), "%" + key + "%"),
                            criteriaBuilder.like(root.<String>get("parentName"), "%" + key + "%"),
                            criteriaBuilder.like(root.<String>get("createByName"), "%" + key + "%"),
                            criteriaBuilder.like(root.<String>get("tenantCode"), "%" + key + "%")));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
    }
}
