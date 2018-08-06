package com.arcvideo.pgcliveplatformserver.tmservice;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.*;
import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.w3c.dom.*;


/**
 * @author Morgan Wang
 * @version 1.0
 */

public class UtilHelper {
	private static Logger logger = Logger.getLogger(UtilHelper.class.getName());
	
	public static boolean outputXml2Response(Node srcXmlNode, String templetFilePath, ServletOutputStream out) {
		if (srcXmlNode != null)  {
			try {
				Source srcXsl = null;
				if (templetFilePath != null) {
					try {
						InputSource isXsl = new InputSource(new FileReader(templetFilePath));
						srcXsl = new SAXSource(isXsl);
					} catch (Exception e) {
						srcXsl = null;
					}
				}
				
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer trans = (srcXsl==null) ? factory.newTransformer() : factory.newTransformer(srcXsl);

				Source srcXml = new DOMSource(srcXmlNode);
				trans.transform(srcXml, new StreamResult(out));
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.toString(), e);
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

    /**
     * Get a local network interface that is in the same network segment as netAddr
     */
    public static NetworkInterface getNetworkInterface(InetAddress netAddr) {
    	NetworkInterface niResult = null;
    	try {
    		if (netAddr != null) {
	        	byte[] addrBytes = netAddr.getAddress();
	        	if (addrBytes != null && addrBytes.length >= 4) {
		        	long l0 = (addrBytes[0] >= 0) ? addrBytes[0] : (addrBytes[0]+256);
		        	long l1 = (addrBytes[1] >= 0) ? addrBytes[1] : (addrBytes[1]+256);
		        	long l2 = (addrBytes[2] >= 0) ? addrBytes[2] : (addrBytes[2]+256);
		        	long l3 = (addrBytes[3] >= 0) ? addrBytes[3] : (addrBytes[3]+256);
		        	long lNetAddr = (l0<<24) | (l1<<16) | (l2<<8) | l3;
		        	long lAddr;
		        	short maskLength;
		        	InetAddress addr;
		        	InterfaceAddress iAddr;
		        	NetworkInterface ni;
		        	Iterator<InterfaceAddress> iter;
		        	ArrayList<NetworkInterface> niList = new ArrayList<NetworkInterface>();
		    		Enumeration<NetworkInterface> nin = NetworkInterface.getNetworkInterfaces();
		    		while (nin.hasMoreElements()) {
		    			try {
		    				ni = nin.nextElement();
		    				iter = ni.getInterfaceAddresses().iterator();
		    				while (iter.hasNext()) {
		        				iAddr = iter.next();
		        				addr = iAddr.getAddress();
		        				maskLength = iAddr.getNetworkPrefixLength();
		        				if (maskLength > 0 && maskLength < 32 && (addr instanceof Inet4Address)) {
		        					addrBytes = addr.getAddress();
		        		        	if (addrBytes != null && addrBytes.length >= 4) {
			        		        	l0 = (addrBytes[0] >= 0) ? addrBytes[0] : (addrBytes[0]+256);
			        		        	l1 = (addrBytes[1] >= 0) ? addrBytes[1] : (addrBytes[1]+256);
			        		        	l2 = (addrBytes[2] >= 0) ? addrBytes[2] : (addrBytes[2]+256);
			        		        	l3 = (addrBytes[3] >= 0) ? addrBytes[3] : (addrBytes[3]+256);
			        		        	lAddr = (l0<<24) | (l1<<16) | (l2<<8) | l3;
			        					if ((lAddr>>(32-maskLength))==(lNetAddr>>(32-maskLength))) {
			        						niList.add(ni);
			        					}
		        		        	}
		        				}
		    				}
		    			} catch (Exception e) {
		    				logger.log(Level.SEVERE, e.toString(), e);
		    			}
		    		}
		    		if (niList.size() > 0) {
		    			Random rand = new Random();
		    			niResult = niList.get(rand.nextInt(niList.size()));
		    			rand = null;
		    		}
		    		niList = null;
		    		nin = null;
	        	}
    		}
    		if (niResult==null) {
    			niResult = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
    		}
    	} catch (Exception e) {
    		logger.log(Level.SEVERE, e.toString(), e);
    	}
    	
    	return niResult;
    }
    
    public static HostBean parseHost(String path) {
    	String host = null;
    	String srcAddress = null;
    	String localIf = null;
		Integer port = null;
		int index = -1;

		// "udp://srcAddress@destAddress:portNumber"
		host = path.substring(6);
		index = host.indexOf('/');
		if (index < 0) {
			index = host.indexOf('?');
		}
		if (index >= 0) {
			host = host.substring(0, index);
		}
        int at = host.lastIndexOf('@');
        if (at >= 0) {
        	srcAddress = host.substring(0, at);
            host = host.substring(at+1);
        }
		index = host.indexOf(':');
		if (index >= 0) {
			port = Integer.parseInt(host.substring(index+1));
			host = host.substring(0, index);
		} else {
			port = Integer.parseInt(host);
			host = "";
		}

		index = path.indexOf("localaddr=");
		if (index > 0) {
			localIf = path.substring(index+10);
			index = localIf.indexOf('&');
			if (index >= 0) {
				localIf = localIf.substring(0, index);
			}
		}
		
		HostBean hostBean = new HostBean();
		hostBean.setHost(host);
		hostBean.setPort(port);
		hostBean.setSrcAddress(srcAddress);
		hostBean.setLocalIf(localIf);
		return hostBean;
    }
}
