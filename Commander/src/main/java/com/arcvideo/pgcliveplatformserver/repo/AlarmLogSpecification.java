package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.AlarmLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlarmLogSpecification {
    public static Specification<AlarmLog> searchKeyword(final String key, final Date startTime, final Date endTime) {
        return new Specification<AlarmLog>() {
            @Override
            public Predicate toPredicate(Root<AlarmLog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (StringUtils.isNotEmpty(key)) {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(root.<String>get("description"), "%" + key + "%"),
                            criteriaBuilder.like(root.<String>get("exceptionMessage"), "%" + key + "%")));
                }
                if (startTime != null && endTime != null){
                    predicates.add(criteriaBuilder.and(
                            criteriaBuilder.greaterThan(root.<Date>get("createTime"), startTime),
                            criteriaBuilder.lessThan(root.<Date>get("createTime"), endTime)
                    ));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
    }
}
