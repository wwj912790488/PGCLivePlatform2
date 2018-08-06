package com.arcvideo.pgcliveplatformserver.specfication;

import com.arcvideo.pgcliveplatformserver.entity.SupervisorTask;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zfl on 2018/5/4.
 */
public class SupervisorTaskSpecfication {
    public static Specification<SupervisorTask> listWithoutIsDelete() {
        return new Specification<SupervisorTask>() {
            @Override
            public Predicate toPredicate(Root<SupervisorTask> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.and(
                            criteriaBuilder.equal(root.<Boolean>get("isDeleted"), false)));
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
    }
}
