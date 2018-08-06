package com.arcvideo.pgcliveplatformserver.tmservice;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatagramDistributor {
	private static Logger logger = Logger.getLogger(DatagramDistributor.class.getName());
	private HashMap<String, DataDispatcher> g_mapDispatcher = new HashMap<String, DataDispatcher>();
	private Lock m_lockDispatcher = new ReentrantLock();
	
	public DatagramDistributor() {
	}
	
	private DatagramSocket generateDatagramSocket(String path) {
		DatagramSocket datagramSocket = null;
		
		if(path.contains(DataDispatcher.KEY_MEDIAFORMAT + "=" + DataDispatcher.VALUE_FLV)) {
			return null;
		}
		
		HostBean hostBean = UtilHelper.parseHost(path);
		String udpHost = hostBean.getHost();
		Integer udpPort = hostBean.getPort();
		String srcAddress = hostBean.getSrcAddress();
		String localIf = hostBean.getLocalIf();
		
		if (udpHost == null || udpHost.isEmpty() || udpHost.compareTo("*") == 0) {
    		udpHost = "0.0.0.0";
    	}
    	if (localIf == null || localIf.isEmpty() || localIf.compareTo("*") == 0) {
    		localIf = "0.0.0.0";
    	}
    	
    	try {
	    	InetAddress addr = InetAddress.getByName(udpHost);
	    	if (addr.isMulticastAddress()) {
		    	InetAddress loAddr = InetAddress.getByName(localIf); 
	    		SocketAddress mcSockAddr = new InetSocketAddress(addr, udpPort);
	    		SocketAddress loSockAddr = new InetSocketAddress(loAddr, udpPort);
	    		NetworkInterface loNetIf = NetworkInterface.getByInetAddress(loAddr);
	    		InetAddress srcInetAddress = null;
	    		if (srcAddress != null && srcAddress.length() > 0) {
	    			srcInetAddress = InetAddress.getByName(srcAddress);
	    		}
	    		
	    		if (srcInetAddress != null) {
	    			MembershipKey key = null;
	    			DatagramChannel dc = null;
	    			try {
	    				/* dc will be closed in DataDispatcher */
						dc = DatagramChannel.open(StandardProtocolFamily.INET).setOption(StandardSocketOptions.SO_REUSEADDR, true);
						if (dc != null) {
							try {
								dc.bind(mcSockAddr);
							} catch (Exception e) {
								dc.bind(loSockAddr);
			    			}
							key = dc.join(addr, 
								(loNetIf == null ? UtilHelper.getNetworkInterface(srcInetAddress) : loNetIf), 
								srcInetAddress);
						}
	    			} catch (Exception e) {
	    				if (dc != null) {
							try {
								dc.close();
							} catch (IOException e1) {
								logger.log(Level.SEVERE, e.toString(), e1);
							} finally {
								dc = null;
							}
						}
	    				logger.log(Level.SEVERE, e.toString(), e);
	    			} finally {
						if(dc != null) {
							datagramSocket = dc.socket();
						}
	    			}
	    		} else {
					MulticastSocket multicastSocket = null;
	    			try {
	    				try {
	    					/* multicastSocket will be closed in DataDispatcher */
	    					multicastSocket = new MulticastSocket(mcSockAddr);
	    				} catch (Exception e) {
	    					/* multicastSocket will be closed in DataDispatcher */
	    					multicastSocket = new MulticastSocket(loSockAddr);
	    				}
	    				if (multicastSocket != null) {
				    		if (loNetIf == null) {
				    			multicastSocket.joinGroup(addr);
				    		} else {
				    			multicastSocket.joinGroup(mcSockAddr, loNetIf);
				    		}
	    				}
	    			}
	    			catch (Exception e) {
	    				if (multicastSocket != null) {
	    					multicastSocket.close();
	    					multicastSocket = null;
	    				}
	    				logger.log(Level.SEVERE, e.toString(), e);
	    			} finally {
		    			if (multicastSocket != null) {
		    				datagramSocket = multicastSocket;
		    			}
	    			}
	    		}
	    	} else {
	    		datagramSocket = new DatagramSocket(udpPort, addr);
	    	}
    	} catch(Exception e) {
    		logger.log(Level.SEVERE, e.toString(), e);
    	}
    	
    	return datagramSocket;
	}
	
	/**
	 * 
	 * @param path
	 * @param out
	 * @return 0: success
	 */
	private int addPathOutput(String path, OutputStream out) {
		logger.info("addPathOutput : path=" + path + " out=" + out.toString());
		m_lockDispatcher.lock();
		int ret = 0;
		
		try {
			DataDispatcher dataDispatcher = g_mapDispatcher.get(path);
			if (dataDispatcher == null) {
				DatagramSocket datagramSocket = generateDatagramSocket(path);
				dataDispatcher = new DataDispatcher(datagramSocket, path);
				g_mapDispatcher.put(path, dataDispatcher);
			}
			
			DataOutputIf dataOutput = dataDispatcher.GetOutputIf(out);
			if(dataOutput == null) {
				dataOutput = new DataOutputStream(dataDispatcher, out, path);
				if (!dataDispatcher.RegisterDataOutput(dataOutput)) {
					throw new Exception("Regist dataOutput faled.");
				}
			}
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
			ret = 1;
		} finally {
			m_lockDispatcher.unlock();
		}
		
		return ret;
	}
	
	private void removePathOutput(String path, OutputStream out) {
		m_lockDispatcher.lock();
		
		try {
			DataDispatcher dataDispatcher = g_mapDispatcher.get(path);
			if (dataDispatcher != null) {
				DataOutputIf outIf = dataDispatcher.GetOutputIf(out);
				dataDispatcher.UnregisterDataOutput(outIf);
				if (dataDispatcher.IsOutputEmpty()) {
					g_mapDispatcher.remove(path);
					dataDispatcher.Destroy();
					dataDispatcher = null;
				}
			}
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		} finally {
			m_lockDispatcher.unlock();
		}
		
		logger.info("removePathOutput : path=" + path + " out=" + out.toString());
	}
	
	private long sendData(String path, OutputStream out) {
		long ret = 0;
		try {
			DataDispatcher dataDispatcher = g_mapDispatcher.get(path);
			if (dataDispatcher == null) {
				throw new Exception("dataDispatcher is not found");
			}
			
			DataOutputIf dataOutput = dataDispatcher.GetOutputIf(out);
			if (dataOutput == null) {
				throw new Exception("dataOutput is not found");
			}
			
			ret = dataOutput.sendoutProcedureAsync();
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		
		return ret;
	}
	
	public void processPathOutput(String path, OutputStream out) {
		addPathOutput(path, out);
		long sendCount = sendData(path, out);
		removePathOutput(path, out);
		logger.info(String.format("uri=%s, out=%s, sendCount=%d", path, out.toString(), sendCount));
	}
}
