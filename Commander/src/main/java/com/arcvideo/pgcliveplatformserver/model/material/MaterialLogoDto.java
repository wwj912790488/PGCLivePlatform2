package com.arcvideo.pgcliveplatformserver.model.material;

import com.arcvideo.pgcliveplatformserver.entity.MaterialIcon;
import com.arcvideo.pgcliveplatformserver.entity.MaterialLogo;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by slw on 2018/8/3.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MaterialLogoDto {
    private Long id;
    private String name;

    public MaterialLogoDto() {
    }

    public MaterialLogoDto(MaterialLogo icon) {
        this.id = icon.getId();
        this.name = icon.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
