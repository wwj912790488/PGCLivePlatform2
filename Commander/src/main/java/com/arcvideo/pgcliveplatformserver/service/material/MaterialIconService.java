package com.arcvideo.pgcliveplatformserver.service.material;

import com.arcvideo.pgcliveplatformserver.entity.MaterialIcon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Created by zfl on 2018/5/4.
 */
public interface MaterialIconService {
    List<MaterialIcon> listMaterial(Specification<MaterialIcon> specification);
    Page<MaterialIcon> listMaterial(Pageable pageable);
    Page<MaterialIcon> listMaterial(Specification<MaterialIcon> specification, Pageable pageable);
    MaterialIcon save(MaterialIcon materialIcon);
    void delete(String ids);
    void delete(Long id);
}
