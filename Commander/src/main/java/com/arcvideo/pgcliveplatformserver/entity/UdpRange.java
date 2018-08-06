package com.arcvideo.pgcliveplatformserver.entity;

import javax.persistence.*;

/**
 * Created by slw on 2018/6/22.
 */
@Entity
@Table(name = "udp_range")
public class UdpRange {
    public static final long IP_BEGIN=3758096640L;  //224.0.1.0
    public static final long IP_END=4026531839L;    //239.255.255.255
    public static final int PORT_BEGIN=1;
    public static final int PORT_END=65535;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "ip_begin")
    private String ipBegin;

    @Column(name = "ip_end")
    private String ipEnd;

    @Column(name = "port_begin")
    private Integer portBegin;

    @Column(name = "port_end")
    private Integer portEnd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIpBegin() {
        return ipBegin;
    }

    public void setIpBegin(String ipBegin) {
        this.ipBegin = ipBegin;
    }

    public String getIpEnd() {
        return ipEnd;
    }

    public void setIpEnd(String ipEnd) {
        this.ipEnd = ipEnd;
    }

    public Integer getPortBegin() {
        return portBegin;
    }

    public void setPortBegin(Integer portBegin) {
        this.portBegin = portBegin;
    }

    public Integer getPortEnd() {
        return portEnd;
    }

    public void setPortEnd(Integer portEnd) {
        this.portEnd = portEnd;
    }
}
