package com.arcvideo.pgcliveplatformserver.service.user;

import com.arcvideo.pgcliveplatformserver.entity.UTenants;
import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public interface TenantsService {

    /**
     * 以companyId为KEY,Utenants对象为值
     * @return
     */
    Map<String,UTenants> findForMap();

    UTenants saveTenant(UTenants uTenant);

    boolean saveTenants(List<UTenants> list);

    UTenants updateTenant(UTenants uTenant);

    Page<UTenants> list(Specification<UTenants> specification, PageRequest pageRequest);

    List<UTenants> findByUser(User user);

    UTenants findOneByCompanyId(String companyId);

    UTenants findOneById(Long id);

    int removeTenant(Set<Long> ids);

    ResultBean verifyDelete(Set<Long> ids);

    int countByCompanyName(String companyName);


}
