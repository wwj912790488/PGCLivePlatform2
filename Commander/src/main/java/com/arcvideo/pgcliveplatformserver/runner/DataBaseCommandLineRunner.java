package com.arcvideo.pgcliveplatformserver.runner;

import com.arcvideo.pgcliveplatformserver.entity.URole;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.repo.MenuRepo;
import com.arcvideo.pgcliveplatformserver.repo.RoleRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

@Component
@Order(value=2)
public class DataBaseCommandLineRunner implements CommandLineRunner {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MenuRepo menuRepo;
    @Autowired
    RoleRepo roleRepo;

    @Value("${spring.mvc.locale}")
    private String localeString;

    @Override
    public void run(String... strings) throws Exception {
        LocaleContextHolder.setLocale(new Locale(localeString));
        insertRole();
    }

    /**
     * 插入动态角色
     */
    private void insertRole() {
        URole role = roleRepo.findByRoleType(RoleType.Admin);
        if (role == null) {
            URole saveRole = new URole();
            saveRole.setRoleName(RoleType.Admin.getMessageKey());
            saveRole.setMenuIds("3,4,6,7,8,9,");
            saveRole.setCreateTime(new Timestamp(new Date().getTime()));
            saveRole.setRoleType(RoleType.Admin);
            roleRepo.save(saveRole);
            logger.debug(RoleType.Admin.getMessageKey() + "被插入");
        }
    }
}
