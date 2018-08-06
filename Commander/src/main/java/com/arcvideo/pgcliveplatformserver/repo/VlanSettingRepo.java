package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.VlanSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by slw on 2018/7/3.
 */
public interface VlanSettingRepo extends JpaRepository<VlanSetting, Long>, JpaSpecificationExecutor<VlanSetting> {
    VlanSetting findFirstByNioTypeContaining(String nioType);
    VlanSetting findFirstByCidr(String cidr);
    VlanSetting findFirstByName(String name);
}
