package com.arcvideo.pgcliveplatformserver.service.user;

import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.model.user.UserResult;
import com.arcvideo.pgcliveplatformserver.model.user.UserSaveCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService {

    User findById(Long id);

    List<User> findAll();

    Map<String,User> findMapUserAll();

    User createUser(UserSaveCommand userSaveCommand);

    User editUser(UserSaveCommand userSaveCommand);

    List<User> save(List<User> userList);

    void updateRoleType(RoleType roleType,long id);

    void changePassword(Long id,String password);

    void settingAdministrator(long id);
    ResultBean setTenantAdmin(String sttingUserId, String userId);

    Page<User> listUser(Specification<User> specification, PageRequest pageRequest);

    boolean removeUserByUserId(String userId);

    User findDisabledByName(String name);

    User findNotDisabledByName(String name);

    User findByUserId(String userId);

    List<User> findByUserIds(List<String> userIdList,boolean isDisabled);

    Page<UserResult> list(User user,String keyword,PageRequest pageRequest);

    List<User> findByCompanyId(Set<String> companyIds);

    void disable(String userId);

    void updateCompanyNameByCompanyId(String companyName,String companyId);

}
