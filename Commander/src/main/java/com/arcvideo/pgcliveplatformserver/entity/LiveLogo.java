package com.arcvideo.pgcliveplatformserver.entity;

import com.arcvideo.pgcliveplatformserver.model.PositionType;

import javax.persistence.*;

/**
 * Created by slw on 2018/6/2.
 */
@Entity
@Table(name = "live_logo")
public class LiveLogo {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "pos_type")
    @Enumerated(EnumType.STRING)
    private PositionType posType;

    @Column(name = "pos_x")
    private int posX;

    @Column(name = "pos_y")
    private int posY;

    @Column(name = "resize")
    private float resize = 100;

    @Column(name = "material_id")
    private Long materialId;

    @Column(name = "content_id")
    private Long contentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PositionType getPosType() {
        return posType;
    }

    public void setPosType(PositionType posType) {
        this.posType = posType;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public float getResize() {
        return resize;
    }

    public void setResize(float resize) {
        this.resize = resize;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }
}
