package com.arcvideo.pgcliveplatformserver.model.storage;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "storage")
public class StorageDto {

    private String name;
    private String path;
    private String type;
    private String user;
    private String pwd;
    private String options;
    private Boolean mounted;

    public StorageDto() {
    }

    public StorageDto(String name, String path, String type) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.mounted = true;
    }

    public StorageDto(String name, String path, String type, String user, String pwd) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.user = user;
        this.pwd = pwd;
        this.mounted = true;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public Boolean getMounted() {
        return mounted;
    }

    public void setMounted(Boolean mounted) {
        this.mounted = mounted;
    }
}
