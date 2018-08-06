package com.arcvideo.pgcliveplatformserver.entity;

import javax.persistence.*;

/**
 * Created by slw on 2018/7/3.
 */
@Entity
@Table(name = "vlan_setting")
public class VlanSetting {

    public enum NioType {
        CONVENE_IN("汇聚输入"),
        CONVENE_OUT("汇聚输出"),
        DELAYER_IN("延时输入"),
        DELAYER_OUT("延时输出"),
        LIVE_IN("在线输入"),
        LIVE_OUT("在线输出");

        private final String key;

        NioType(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public static final NioType[] ALL = { CONVENE_IN, CONVENE_OUT, DELAYER_IN, DELAYER_OUT, LIVE_IN, LIVE_OUT };
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "tag")
    private String tag;

    @Column(name = "cidr")
    private String cidr;

    @Column(name = "nio_type")
    private String nioType;

    @Column(name = "note")
    private String note;

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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCidr() {
        return cidr;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    public String getNioType() {
        return nioType;
    }

    public void setNioType(String nioType) {
        this.nioType = nioType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
