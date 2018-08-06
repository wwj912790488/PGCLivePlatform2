package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.Content;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by slw on 2018/3/21.
 */
public class ContentSpecfication  {
    public static Specification<Content> searchByConditions(final String key, final Content.Status status, final String companyId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotEmpty(key)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.<String>get("name"), "%" + key + "%")));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.<Content.Status>get("status"), status)));
            }

            if (StringUtils.isNotBlank(companyId)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.<String>get("companyId"), companyId)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }

    public static Specification<Content> searchByConditions(final String name) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotEmpty(name)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.<String>get("name"), "%" + name + "%")));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }
}
