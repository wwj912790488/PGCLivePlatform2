package com.arcvideo.pgcliveplatformserver.specfication;

import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.LiveTask;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zfl on 2018/5/4.
 */
public class LiveTaskSpecfication {
    public static Specification<LiveTask> listWithoutIsDelete() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }

    public static Specification<LiveTask> searchByConditions(String key,Long contentId,Integer status) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(StringUtils.isNotEmpty(key)){
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.<String>get("name"), "%" + key + "%")));
            }
            if(contentId!=null){
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(root.<Content>get("content").<Long>get("id"), contentId)
                        )
                );
            }

            if(status!=null){
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(root.<Integer>get("liveTaskStatus"), status)
                        )
                );
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }
}
