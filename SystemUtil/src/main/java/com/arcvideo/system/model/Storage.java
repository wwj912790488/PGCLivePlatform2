package com.arcvideo.system.model;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Storage{
    private Long id = 0L;
    private String name;
    private String path;
    private String type;
    private String user;
    private String password;
    private String options;
    private Boolean mounted = true;

    public Storage() {
    }

    public Storage(String name, String path, String type) {
        this.name = name;
        this.path = path;
        this.type = type;
    }

    public Storage(String name, String path, String type, String user, String password) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.user = user;
        this.password = password;
    }

    public Storage(Long id, String name, String path, String type, String user, String password, String options, Boolean mounted) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.type = type;
        this.user = user;
        this.password = password;
        this.options = options;
        this.mounted = mounted;
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

    public boolean isMounted() {
        return mounted;
    }

    public void setMounted(boolean mounted) {
        this.mounted = mounted;
    }

    public boolean isSame(Storage storage) {
        return name.equals(storage.getName()) && path.equals(storage.getPath()) && type.equals(storage.getType());
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(11, 21);
        builder.append(name);
        builder.append(path);
        builder.append(user);
        builder.append(password);
        builder.append(type);
        builder.append(options);
        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof Storage)) {
            return false;
        }

        Storage storage = (Storage) obj;
        return name.equals(storage.getName()) && path.equals(storage.getPath()) && user.equals(storage.getUser())
                && password.equals(storage.getPassword()) && type.equals(storage.getType())
                && options.equals(storage.getOptions());
    }

    @Override
    public String toString() {
        return "Storage{" +
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
