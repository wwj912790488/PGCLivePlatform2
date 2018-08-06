package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.UTenants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.util.Set;

public interface TenantsRepo extends JpaRepository<UTenants,Long> ,JpaSpecificationExecutor<UTenants> {

    UTenants findOneByCompanyId(String companyId);

    int countByCompanyName(String companyName);

    @Modifying
    @Transactional
    int deleteByIdIn(Set<Long> ids);

}
