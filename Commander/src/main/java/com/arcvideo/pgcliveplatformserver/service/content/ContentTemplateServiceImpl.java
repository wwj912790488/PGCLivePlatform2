package com.arcvideo.pgcliveplatformserver.service.content;

import com.arcvideo.pgcliveplatformserver.entity.Channel;
import com.arcvideo.pgcliveplatformserver.entity.ContentTemplate;
import com.arcvideo.pgcliveplatformserver.entity.LiveOutput;
import com.arcvideo.pgcliveplatformserver.model.CommonConstants;
import com.arcvideo.pgcliveplatformserver.model.content.ChannelItemDto;
import com.arcvideo.pgcliveplatformserver.model.content.ContentItemDto;
import com.arcvideo.pgcliveplatformserver.model.content.ContentTempDto;
import com.arcvideo.pgcliveplatformserver.repo.ContentTemplateRepo;
import com.arcvideo.pgcliveplatformserver.repo.LiveOutputRepo;
import com.arcvideo.pgcliveplatformserver.security.SecurityUser;
import com.arcvideo.pgcliveplatformserver.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Content Template Service  implements
 *
 * @author lgq on 2018/6/4.
 * @version 1.0
 */
@Service
public class ContentTemplateServiceImpl implements ContentTemplateService {

    private  final static  Logger logger = LoggerFactory.getLogger(ContentTemplateServiceImpl.class);

    @Autowired
    private ContentTemplateRepo templateRepo;

    @Autowired
    private LiveOutputRepo liveOutputRepo ;

    private final static Integer DEFAULT_TYPE = 1;

    @Override
    public Page<ContentTemplate> list(Specification<ContentTemplate> specification, Pageable page) {

        return templateRepo.findAll(specification, page);
    }


    @Override
    public List<ContentTemplate> listByType(Integer type){
        return templateRepo.findAllByType(type);
    }

    @Override
    public ContentTemplate find(Long id) {

        return templateRepo.findOne(id);
    }

    @Transactional
    @Override
    public ContentTemplate save(ContentTemplate template) {

        template.setCreateTime(new Date());
        SecurityUser securityUser =  UserUtil.getLoginUser();
        template.setCreateUserId(securityUser.getUserId());
        template.setCompanyId(UserUtil.getSsoCompanyId());
        return templateRepo.save(template);
    }

    @Transactional
    @Override
    public ContentTemplate update(ContentTemplate template) {
        ContentTemplate old = this.find(template.getId());
        if(old != null){
            template.setCreateTime(old.getCreateTime());
            template.setCreateUserId(old.getCreateUserId());
            template.setCompanyId(old.getCompanyId());
        }
        return templateRepo.saveAndFlush(template);

    }

    @Transactional
    @Override
    public void delete(Long id) {

        templateRepo.delete(id);

    }


    @Override
    public List<LiveOutput> liveOutListUsedTemp(String templateIds) {
        List<LiveOutput> all = new ArrayList<>();
        String[] idList = templateIds.trim().split(",");
        for (String id : idList) {
            List<LiveOutput> usedContents = liveOutputRepo.findByTemplateId(Long.valueOf(id));
            all.addAll(usedContents);
        }
        return all;
    }

    @Override
    public List<ContentTemplate> all(Specification<ContentTemplate> specification) {
        return templateRepo.findAll(specification);
    }
}
