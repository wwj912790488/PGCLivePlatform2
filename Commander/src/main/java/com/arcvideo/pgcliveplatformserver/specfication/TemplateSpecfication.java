package com.arcvideo.pgcliveplatformserver.specfication;

import com.arcvideo.pgcliveplatformserver.entity.ContentTemplate;
import com.arcvideo.pgcliveplatformserver.util.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Template  Specfication
 *
 * @author lgq on 2018/6/5.
 * @version 1.0
 */
public class TemplateSpecfication {

    public static Specification<ContentTemplate> searchKeyword(final String key) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotEmpty(key)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.<String>get("name"), "%" + key + "%"),
                        criteriaBuilder.like(root.<String>get("displayName"), "%" + key + "%")));
            }
            if(!UserUtil.isAdminstrator() && StringUtils.isNotEmpty(UserUtil.getSsoCompanyId())){

                List<Predicate> pes = new ArrayList<>();

                pes.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.<String>get("companyId"), UserUtil.getSsoCompanyId())));

                pes.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.<String>get("type"), 1)));

                predicates.add(criteriaBuilder.or(pes.toArray(new Predicate[]{})));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }

    public static Specification<ContentTemplate> findAllPermitted() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!UserUtil.isAdminstrator()) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.<String>get("companyId"), UserUtil.getSsoCompanyId()),
                        criteriaBuilder.equal(root.<String>get("type"), 1)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }

    public static Specification<ContentTemplate> searchByConditions(final String name) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(name)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.<String>get("name"), "%" + name + "%")
                ));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }
}
