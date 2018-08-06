package com.arcvideo.pgcliveplatformserver.specfication;

import com.arcvideo.pgcliveplatformserver.entity.MaterialIcon;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zfl on 2018/5/4.
 */
public class MaterialIconSpecfication {
    public static Specification<MaterialIcon> listByType() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }

    public static Specification<MaterialIcon> listByTypeAndCompanyId( final String companyId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(companyId)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.<String>get("companyId"), companyId)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }

    public static Specification<MaterialIcon> listByTypeAndCompanyIdAndKeyword( final String companyId, final  String keyword) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(companyId)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.<String>get("companyId"), companyId)));
            }
            if (StringUtils.isNotBlank(keyword)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.<String>get("name"), "%" + keyword + "%")));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }

    public static Specification<MaterialIcon> searchByConditions(final String name) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(name)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.<String>get("name"), "%" + name + "%")));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }
}
