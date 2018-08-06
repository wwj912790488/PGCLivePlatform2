package com.arcvideo.pgcliveplatformserver.service.content;

import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.IpSwitchTask;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.content.ContentItemDto;
import com.arcvideo.pgcliveplatformserver.model.content.ContentTableModel;
import com.arcvideo.pgcliveplatformserver.model.dashboard.ContentInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Created by slw on 2018/3/21.
 */
public interface ContentService {
    Page<Content> listContent(Pageable page);

    List<Content> findByAllContentList();

    public List<Content> findByRunningContentList(Content.Status status);

    List<Content> listContent(Specification specification);

    Page<Content> listContent(Specification<Content> specification, Pageable page);

    List<Content> listContent(List<Long> contentIds);

    List<Content> listContentDetail(List<Content> contents);

    Content contentDetail(Content content);

    List<ContentTableModel> Convert2ContentTableModel(List<Content> contents);

    List<ContentItemDto> convert2ContentItemDto(List<Content> contents);

    Content findById(Long contentId);

    Boolean startContent(Long contentId);

    Boolean stopContent(Long contentId);

    Boolean removeContent(Long contentId);

    ResultBean addContent(Content content);

    ResultBean updateContent(Content content);

    ResultBean switchChannel(IpSwitchTask.Type type, Long contentId);

    ContentInfo getContentInfo();
}
