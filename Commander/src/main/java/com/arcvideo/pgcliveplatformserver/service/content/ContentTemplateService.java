package com.arcvideo.pgcliveplatformserver.service.content;

import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.ContentTemplate;
import com.arcvideo.pgcliveplatformserver.entity.LiveOutput;
import com.arcvideo.pgcliveplatformserver.model.content.ContentTempDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;

public interface ContentTemplateService  {


    /**
     * Template list
     * @param specification
     *
     *
     * @return
     */
    Page<ContentTemplate> list(Specification<ContentTemplate> specification, Pageable page);

    /**
     *  find list by type
     *  1:default template
     *  0:customer template
     * @param type
     * @return
     */
    List<ContentTemplate> listByType(Integer type);

    /**
     * find by id
     * @param id
     * @return
     */
    ContentTemplate find(Long id);


    /**
     * save template
     * @param template
     * @return ContentTemplate
     */
    ContentTemplate save(ContentTemplate template);


    /**
     * update template by id
     * @param template
     * @return
     */
    ContentTemplate update(ContentTemplate template);


    /**
     * delete template by id
     * @param id
     */
    void delete(Long id);


    /**
     *
     * @param templateIds
     * @return
     */
    List<LiveOutput> liveOutListUsedTemp(String templateIds);

    /**
     * all list
     * @param specification
     * @return
     */
    List<ContentTemplate> all(Specification<ContentTemplate> specification);

}
