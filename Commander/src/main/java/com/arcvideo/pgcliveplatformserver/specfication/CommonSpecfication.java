package com.arcvideo.pgcliveplatformserver.specfication;

import com.arcvideo.pgcliveplatformserver.util.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zfl on 2018/6/13.
 */
public class CommonSpecfication{

    public static Specification findAllPermitted() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            String companyId = null;
            if (!UserUtil.isAdminstrator()) {
                companyId = UserUtil.getSsoCompanyId();
            }
            if (StringUtils.isNotBlank(companyId)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.<String>get("companyId"), companyId)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }
}
