package com.arcvideo.pgcliveplatformserver.service.material;


import com.arcvideo.pgcliveplatformserver.entity.MaterialLogo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Created by zfl on 2018/5/4.
 */
public interface MaterialLogoService {
    List<MaterialLogo> listMaterial(Specification<MaterialLogo> specification);
    Page<MaterialLogo> listMaterial(Pageable pageable);
    Page<MaterialLogo> listMaterial(Specification<MaterialLogo> specification, Pageable pageable);
    MaterialLogo save(MaterialLogo materialIcon);
    void delete(String ids);
    void delete(Long id);
}
