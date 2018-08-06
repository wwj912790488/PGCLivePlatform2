package com.arcvideo.system.model;

public class Route {
	private String destination;
	private String gateway;
	private String mask;
	private String flags;
	private String metric;
	private String ref;
	private String use;
	private String iface;

	public Route() {
	}
		
	public Route(String destination, String mask, String gateway, String iface) {
		this.destination = destination;
		this.mask = mask;
		this.gateway = gateway;
		this.iface = iface;
	}

	public Route(String[] strings) {
		destination = strings[0];
		gateway = strings[1];
		mask = strings[2];
		flags = strings[3];
		metric = strings[4];
		ref = strings[5];
		use = strings[6];
		iface = strings[7];
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getFlags() {
		return flags;
	}

	public void setFlags(String flags) {
		this.flags = flags;
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public String getIface() {
		return iface;
	}

	public void setIface(String iface) {
		this.iface = iface;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((gateway == null) ? 0 : gateway.hashCode());
		result = prime * result + ((mask == null) ? 0 : mask.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Route other = (Route) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (gateway == null) {
			if (other.gateway != null)
				return false;
		} else if (!gateway.equals(other.gateway))
			return false;
		if (mask == null) {
			if (other.mask != null)
				return false;
		} else if (!mask.equals(other.mask))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Route{" +
				"destination='" + destination + '\'' +
				", gateway='" + gateway + '\'' +
				", mask='" + mask + '\'' +
				", flags='" + flags + '\'' +
				", metric='" + metric + '\'' +
				", ref='" + ref + '\'' +
				", use='" + use + '\'' +
				", iface='" + iface + '\'' +
				'}';
	}
}
