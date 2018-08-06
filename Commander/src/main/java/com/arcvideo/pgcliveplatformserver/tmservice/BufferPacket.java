package com.arcvideo.pgcliveplatformserver.tmservice;

final class BufferPacket {
    byte[] buf;
    int length;
    int bufSize;
    
    BufferPacket(int size) {
    	try {
    		this.buf = new byte[size];
        	this.bufSize = size;
    	} catch (Error err) {
    		this.buf = null;
        	this.bufSize = 0;
    	}
    	
    	this.length = 0;
    }
    
    BufferPacket(byte[] bData, int offset, int len) {
    	try {
	    	this.buf = new byte[len];
			System.arraycopy(bData, offset, this.buf, 0, len);
			this.bufSize = this.length = len;
    	} catch (Error err) {
    		this.buf = null;
        	this.bufSize = this.length = 0;
    	} catch (Exception e) {
    		this.buf = null;
        	this.bufSize = this.length = 0;
		}
    }
    
    BufferPacket CopyMe() {
    	BufferPacket bufPacket = null;
    	
		if (this.buf != null && this.length > 0) {
	    	try {
		    	bufPacket = new BufferPacket(this.bufSize);
				System.arraycopy(this.buf, 0, bufPacket.buf, 0, this.length);
				bufPacket.length = this.length;
	    	} catch (Error err) {
	    		bufPacket = null;
	    	} catch (Exception e) {
	    		bufPacket = null;
			}
		}
		
    	return bufPacket;
    }
    
    int Append(byte[] bData, int offset, int len) {
    	if (this.length + len > this.bufSize) {
    		len = this.bufSize - this.length;
    	}
    	
    	if (len > 0) {
    		System.arraycopy(bData, offset, this.buf, this.length, len);
    		this.length += len;
    		return len;
    	} else {
    		return 0;
    	}
    }
    
    boolean IsReady() {
    	return (length == bufSize);
    }
}
