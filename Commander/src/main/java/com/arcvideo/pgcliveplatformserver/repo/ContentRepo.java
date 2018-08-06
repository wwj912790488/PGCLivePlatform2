package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ContentRepo extends JpaSpecificationExecutor<Content>, JpaRepository<Content, Long> {
    List<Content> findByStatus(Content.Status status);
}
