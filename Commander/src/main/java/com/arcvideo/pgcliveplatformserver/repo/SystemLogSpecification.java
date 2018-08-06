package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.SystemLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SystemLogSpecification {
    public static Specification<SystemLog> searchByConditions(final String keyword, final Date startTime, final Date endTime, final String companyId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (startTime != null) {
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.greaterThan(root.<Date>get("createTime"), startTime)
                ));
            }

            if (endTime != null) {
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.lessThan(root.<Date>get("createTime"), endTime)
                ));
            }
            if (StringUtils.isNotBlank(keyword)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.<String>get("description"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.<String>get("username"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.<String>get("operation"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.<String>get("url"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.<String>get("ip"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.<String>get("params"), "%" + keyword + "%")));
            }

            if (StringUtils.isNotBlank(companyId)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.<String>get("companyId"), companyId)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }
}
