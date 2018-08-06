package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByNameIgnoreCaseAndIsDisabled(String name,boolean isDisabled);

    User findByUserId(String userId);

    List<User> findByUserIdInAndIsDisabled(List<String> userIdList,boolean isDisabled);

    @Modifying
    @Transactional
    int deleteByUserId(String userId);

    @Modifying
    @Transactional
    @Query("update User t set t.roleType=?1 where t.id = ?2")
    void updateRoleType(RoleType roleType, Long id);

    List<User> findByCompanyIdInAndIsDisabled(Set<String> companyIds,boolean isDisabled);

    @Modifying
    @Transactional
    @Query("update User t set t.isDisabled=?1 , t.disableTime= ?2 where t.userId = ?3")
    void disable(boolean isDisabled, Timestamp disableTime,String userId);

    @Modifying
    @Transactional
    @Query("update User t set t.companyName=?1 where t.companyId =?2 ")
    void updateCompanyNameByCompanyId(String companyName,String companyId);

}
