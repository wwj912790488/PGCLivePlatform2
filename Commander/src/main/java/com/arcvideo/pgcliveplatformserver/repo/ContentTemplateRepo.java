package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.ContentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface ContentTemplateRepo  extends JpaSpecificationExecutor<ContentTemplate>, JpaRepository<ContentTemplate, Long> {



    List<ContentTemplate> findAllByType(Integer type);
}
