package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.MaterialIcon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by zfl on 2018/3/26.
 */
public interface MaterialIconRepo extends JpaSpecificationExecutor<MaterialIcon>, JpaRepository<MaterialIcon, Long> {

}
