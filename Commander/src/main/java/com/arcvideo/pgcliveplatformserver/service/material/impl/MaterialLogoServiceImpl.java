package com.arcvideo.pgcliveplatformserver.service.material.impl;


import com.arcvideo.pgcliveplatformserver.entity.MaterialLogo;
import com.arcvideo.pgcliveplatformserver.repo.MaterialLogoRepo;
import com.arcvideo.pgcliveplatformserver.service.material.MaterialLogoService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by zfl on 2018/5/4.
 */
@Service
public class MaterialLogoServiceImpl implements MaterialLogoService {

    @Autowired
    private MaterialLogoRepo materialLogoRepo;

    @Override
    public List<MaterialLogo> listMaterial(Specification<MaterialLogo> specification) {
        return materialLogoRepo.findAll(specification);
    }

    @Override
    public Page<MaterialLogo> listMaterial(Pageable pageable) {
        return materialLogoRepo.findAll(pageable);
    }

    @Override
    public Page<MaterialLogo> listMaterial(Specification<MaterialLogo> specification, Pageable pageable) {
        return materialLogoRepo.findAll(specification, pageable);
    }

    @Override
    public MaterialLogo save(MaterialLogo MaterialLogo) {
        return materialLogoRepo.save(MaterialLogo);
    }

    @Override
    public void delete(String ids) {
        String[] idList = ids.trim().split(",");
        for (String id : idList) {
            Long sourceId = Long.parseLong(id);
            MaterialLogo MaterialLogo = materialLogoRepo.findOne(sourceId);
            if (MaterialLogo != null) {
                deleteFile(MaterialLogo.getContent());
                materialLogoRepo.delete(MaterialLogo);
            }
        }
    }

    private void deleteFile(String content) {
        File file = new File(content);
        if (file.exists() && file.isDirectory()) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void delete(Long id) {
        materialLogoRepo.delete(id);
    }
}
