package com.arcvideo.pgcliveplatformserver.model.content;

import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by slw on 2018/8/3.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContentItemDto {
    private Long id;
    private String name;
    private Content.Status status;
    ChannelItemDto master;
    ChannelItemDto slave;

    public ContentItemDto() {
    }

    public ContentItemDto(Content content) {
        this.id = content.getId();
        this.name = content.getName();
        this.status = content.getStatus();
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

    public Content.Status getStatus() {
        return status;
    }

    public void setStatus(Content.Status status) {
        this.status = status;
    }

    public ChannelItemDto getMaster() {
        return master;
    }

    public void setMaster(ChannelItemDto master) {
        this.master = master;
    }

    public ChannelItemDto getSlave() {
        return slave;
    }

    public void setSlave(ChannelItemDto slave) {
        this.slave = slave;
    }
}
