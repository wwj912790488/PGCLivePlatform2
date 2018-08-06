package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.RecorderTask;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by slw on 2018/5/15.
 */
public class RecorderSpecfication {
    public static Specification<RecorderTask> searchKeyword(final String key, final Boolean isDeleted) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotEmpty(key)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.<String>get("fileName"), "%" + key + "%"),
                        criteriaBuilder.like(root.<String>get("outputPath"), "%" + key + "%")));
            }

            if (isDeleted != null) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.<Boolean>get("isDeleted"), isDeleted)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }
}
