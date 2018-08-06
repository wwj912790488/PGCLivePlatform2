package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.ServerSetting;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ServerSettingRepo extends JpaRepository<ServerSetting, Long>, JpaSpecificationExecutor<ServerSetting> {
    ServerSetting findTopByServerType(ServerType serverType);
    List<ServerSetting> findAllByServerType(ServerType serverType);
}
