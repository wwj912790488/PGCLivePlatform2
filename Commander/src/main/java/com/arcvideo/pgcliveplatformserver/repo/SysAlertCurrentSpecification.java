package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.SysAlertCurrent;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by slw on 2018/6/23.
 */
public class SysAlertCurrentSpecification {
    public static Specification<SysAlertCurrent> searchByConditions(final Long contentId, final String type, final String level, final ServerType serverType,
                                                             final Date startTime, final Date endTime, final String keyword, final String companyId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (contentId != null) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.<Long>get("contentId"), contentId),
                        criteriaBuilder.isNull(root.<Long>get("relId"))));
            }

            if (StringUtils.isNotBlank(type)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.<String>get("type"), type)));
            }
            if (StringUtils.isNotBlank(level)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.<String>get("level"), level)));
            }

            if (serverType != null) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.<ServerType>get("serverType"), serverType)));
            }

            if (startTime != null) {
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.greaterThan(root.<Date>get("createdAt"), startTime)
                ));
            }

            if (endTime != null) {
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.lessThan(root.<Date>get("createdAt"), endTime)
                ));
            }

            if (StringUtils.isNotBlank(keyword)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.<String>get("description"), "%" + keyword + "%")));
            }

            if (StringUtils.isNotBlank(companyId)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.<String>get("companyId"), companyId)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }
}
