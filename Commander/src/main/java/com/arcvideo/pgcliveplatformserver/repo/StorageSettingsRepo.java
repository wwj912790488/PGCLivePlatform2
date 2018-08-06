package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.StorageSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface StorageSettingsRepo extends JpaSpecificationExecutor<StorageSettings>, JpaRepository<StorageSettings, Long> {
	List<StorageSettings> findAllByName(String name);
	List<StorageSettings> findAllByMounted(Boolean mounted);
}
