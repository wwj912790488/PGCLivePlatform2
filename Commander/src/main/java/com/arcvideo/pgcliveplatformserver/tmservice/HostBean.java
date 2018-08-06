package com.arcvideo.pgcliveplatformserver.tmservice;

public class HostBean {
	private String host;	//udp ip
	private String srcAddress;	//source address
	private Integer port;	//udp port
	private String localIf;	//local eth 
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getSrcAddress() {
		return srcAddress;
	}
	public void setSrcAddress(String srcAddress) {
		this.srcAddress = srcAddress;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getLocalIf() {
		return localIf;
	}
	public void setLocalIf(String localIf) {
		this.localIf = localIf;
	}
}
