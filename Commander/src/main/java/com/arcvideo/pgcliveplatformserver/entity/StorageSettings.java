package com.arcvideo.pgcliveplatformserver.entity;

import com.arcvideo.system.model.Storage;
import javax.persistence.*;

@Entity
@Table(name = "storage_settings")
public class StorageSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "path")
    private String path;

    @Column(name = "type")
    private String type;

    @Column(name = "user")
    private String user;

    @Column(name = "password")
    private String password;

    @Column(name = "options")
    private String options;

    @Column(name = "mounted")
    private Boolean mounted;

    public StorageSettings() {
    }

    public StorageSettings(Storage storage) {
        this.name = storage.getName();
        this.path = storage.getPath();
        this.type = storage.getType();
        this.user = storage.getUser();
        this.password = storage.getPassword();
        this.options = storage.getOptions();
        this.mounted = false;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Storage toStorage() {
        Storage storage = new Storage(id, name, path, type, user, password, options, mounted);
        return storage;
    }

    @Override
    public String toString() {
        return "StorageSettings{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", type='" + type + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", options='" + options + '\'' +
                ", mounted=" + mounted +
                '}';
    }
}
