package com.arcvideo.pgcliveplatformserver.service.material.impl;

import com.arcvideo.pgcliveplatformserver.entity.MaterialIcon;
import com.arcvideo.pgcliveplatformserver.repo.MaterialIconRepo;
import com.arcvideo.pgcliveplatformserver.service.material.MaterialIconService;
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
public class MaterialIconServiceImpl implements MaterialIconService {

    @Autowired
    private MaterialIconRepo materialIconRepo;

    @Override
    public List<MaterialIcon> listMaterial(Specification<MaterialIcon> specification) {
        return materialIconRepo.findAll(specification);
    }

    @Override
    public Page<MaterialIcon> listMaterial(Pageable pageable) {
        return materialIconRepo.findAll(pageable);
    }

    @Override
    public Page<MaterialIcon> listMaterial(Specification<MaterialIcon> specification, Pageable pageable) {
        return materialIconRepo.findAll(specification, pageable);
    }

    @Override
    public MaterialIcon save(MaterialIcon materialIcon) {
        return materialIconRepo.save(materialIcon);
    }

    @Override
    public void delete(String ids) {
        String[] idList = ids.trim().split(",");
        for (String id : idList) {
            Long sourceId = Long.parseLong(id);
            MaterialIcon materialIcon = materialIconRepo.findOne(sourceId);
            if (materialIcon != null) {
                deleteFile(materialIcon.getContent());
                materialIconRepo.delete(materialIcon);
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
        materialIconRepo.delete(id);
    }
}
