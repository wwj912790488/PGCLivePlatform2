package com.arcvideo.pgcliveplatformserver.tmservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DataOutputStream implements DataOutputIf {
	private static long createCount = 0;
	private static Logger logger = LoggerFactory.getLogger(DataOutputStream.class);
	
	private static final int QUIT_COMMON_EXCEPTION = 0x0200;
	private static final int QUIT_INTERRUPTED_EXCEPTION = 0x0201;
	private static final int QUIT_WRITE_TIMEOUT = 0x0202;
	private static final int QUIT_SOCKET_EXCEPTION = 0x0203;
	private static final int QUIT_BE_STOPED = 0x0204;
	
	private static final int MaxSize4IFrame = 0x500000;
	private static final int MaxSize4Buffer = 10 * 1024 * 1024; //10M
	private static final int SleepInterval = 100;
	private static final int CheckActiveInterval = 10000;
	
	private int m_nQuit = 0;
	private ArrayList<BufferPacket> m_listPacket = null;
	private Lock m_lockBuffer = new ReentrantLock();
	
	private OutputStream m_outStream = null;
	private String	m_uriSource = null;
	private long m_writeCount = 0;
	private DataDispatcher dataDispatcher = null;
	private Long dataOutputStreamKey;
	private List<BufferPacket> mediaPackets = new ArrayList<BufferPacket>();
	private long cacheSize = 0;
	
	DataOutputStream(DataDispatcher dataDispatcher, OutputStream outStream, String uriSource) {
		/*set key in begin of construction*/
		createCount++;
		dataOutputStreamKey = createCount;
		
		m_listPacket = new ArrayList<BufferPacket>();
		this.dataDispatcher = dataDispatcher;
		this.m_outStream = outStream;
		this.m_uriSource = uriSource;
	}

	public void setDataDispatcher(DataDispatcher dataDispatcher) {
		this.dataDispatcher = dataDispatcher;
	}

	public OutputStream GetOutStream() {
		return m_outStream;
	}
	
	public int DistributeData(BufferPacket bufPacket) {
		int bytesOutput = 0;
		BufferPacket bp = null;
		
		if (bufPacket != null && bufPacket.buf != null && bufPacket.length > 0) {
			/*copy packet or else buffer will be changed by other module.*/
			bp = bufPacket.CopyMe();
		} else {
			return 0;
		}
		
		m_lockBuffer.lock();
		try {
			boolean bAdded = m_listPacket.add(bp);
			if(!bAdded) {
				throw new Exception("m_listPacket.add failed, m_listPacket.size=" + m_listPacket.size());
			}
		} catch (Exception e) {
			logger.error("dataOutputStreamKey=" + dataOutputStreamKey, e);
		} finally {
			m_lockBuffer.unlock();
		}
		
		bytesOutput = bp.length;
		cacheSize += bytesOutput;
		
		return bytesOutput;
	}
	
	private byte[] appendMediaBuffer(BufferPacket bufferPacket) throws Exception {
		// add several buffer together
		mediaPackets.add(bufferPacket);
		int size = 0;
		for(BufferPacket pilot : mediaPackets) {
			size += pilot.length;
		}
		
		byte[] packetBuffer = new byte[size];
		int pos = 0;
		for(BufferPacket pilot : mediaPackets) {
			System.arraycopy(pilot.buf, 0, packetBuffer, pos, pilot.length);
			pos += pilot.length;
		}
		
		byte[] appendedBuffer = dataDispatcher.appendMediaInfo(packetBuffer);
		return appendedBuffer;
	}
	
	private void sendOutputStream() {
		while (m_nQuit == 0) {
			try {
				BufferPacket bufPacket = null;
				
				m_lockBuffer.lock();
				try {
					if (!m_listPacket.isEmpty()) {
						bufPacket = m_listPacket.remove(0);
					}
				} catch(Exception e) {
					logger.error("DataOutputStream sendOutputStream error", e);
				} finally {
					m_lockBuffer.unlock();
				}
				
				if (bufPacket != null) {
					byte[] packetBuffer = bufPacket.buf;
					if (m_writeCount == 0 && m_uriSource.contains(DataDispatcher.KEY_MEDIAFORMAT + "=" + DataDispatcher.VALUE_FLV)) {
						//FLV over HTTP need put media info head.
						packetBuffer = appendMediaBuffer(bufPacket);
						if (packetBuffer == null) {
							// appendMediaInfo will return null when the key frame is not found.
							continue;
						}
					}
					
					m_outStream.write(packetBuffer, 0, packetBuffer.length);
					m_outStream.flush();
					
					m_writeCount += packetBuffer.length;
					cacheSize -= bufPacket.length;
				} else {
					Thread.sleep(10);
				}
			} catch(SocketException e) {
				logger.error("DataOutputStream sendOutputStream error", e);
				m_nQuit = QUIT_SOCKET_EXCEPTION;
			} catch(Exception e) {
				logger.error("DataOutputStream sendOutputStream error", e);
				m_nQuit = QUIT_COMMON_EXCEPTION;
			}
		}
	}
	
	//use async send, so watch dog can know when the data is blocked. 
	public long sendoutProcedureAsync() {
		logger.info(String.format("sendoutProcedureAsync begin: m_uriSource=%s, dataOutputStreamKey=%s", m_uriSource, dataOutputStreamKey));
		
		//thread for sendoutProcedure
		Thread sendoutThread = new Thread(new Runnable() {
	        public void run() {
	        	sendOutputStream();
	        }
		});
		sendoutThread.start();
		
		long writeCount = m_writeCount;
		long timeElasped = 0;
		while(m_nQuit == 0) {
			try {
				//m_outStream.write should be called once in SleepInterval.
				Thread.sleep(SleepInterval);
			} catch (InterruptedException e) {
				logger.error("DataOutputStream sendoutProcedureAsync error", e);
				Thread.currentThread().interrupt();
				m_nQuit = QUIT_INTERRUPTED_EXCEPTION;
			}
			
			timeElasped += SleepInterval;
			if (timeElasped >= CheckActiveInterval) {
				if (writeCount == m_writeCount) {
					logger.info(String.format("m_writeCount is not change m_writeCount=%s, cacheSize=%s, dataOutputStreamKey=%s", m_writeCount, cacheSize, dataOutputStreamKey));
					m_nQuit = QUIT_WRITE_TIMEOUT;
				}
				
				//set check point
				writeCount = m_writeCount;
				timeElasped = 0;
			}
		}
		
		logger.info(String.format("sendoutProcedureAsync end: m_nQuit=0x%04x, m_uriSource=%s, dataOutputStreamKey=%s", m_nQuit, m_uriSource, dataOutputStreamKey));
		return m_writeCount;
	}
	
	public void stop() {
		m_nQuit = QUIT_BE_STOPED;
		logger.info(String.format("stop(): m_uriSource=%s, dataOutputStreamKey=%s", m_uriSource, dataOutputStreamKey));
	}
}
