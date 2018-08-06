package com.arcvideo.pgcliveplatformserver.model.live;

/**
 * Created by zfl on 2018/5/3.
 */
public class LiveContent {
    private Long id;
    private String name;

    public LiveContent() {
    }

    public LiveContent(Long id, String name) {
        this.id = id;
        this.name = name;
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
