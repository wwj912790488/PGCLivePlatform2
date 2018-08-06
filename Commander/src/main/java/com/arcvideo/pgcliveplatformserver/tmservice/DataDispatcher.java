package com.arcvideo.pgcliveplatformserver.tmservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

final class DataDispatcher {
	private static Logger logger = Logger.getLogger(DataDispatcher.class.getName());
	private static long createCount = 0;
	
	public static String KEY_MEDIAFORMAT = "mediaformat";
	public static String VALUE_FLV = "flv";
	
	private static int STAT_COMMON_EXCEPTION = 0x0001;
	private static int STAT_SOCKET_TIMEOUT_EXCEPTION = 0x0002;
	private static int STAT_READ_FILE_ERROR = 0x0003;
	private static int STAT_READ_FILE_TIMEOUT = 0x0004;
	private static int STAT_IO_EXCEPTION = 0x0005;
	private static int STAT_INTERRUPTED_EXCEPTION = 0x0006;
	private static int STAT_BE_DESTROYED = 0x0007;
	
	private static final int SOURCE_TYPE_UDP = 0;
	private static final int SOURCE_TYPE_FILE = 1;
	
	/* package should be large enough to eliminate UDP package lost.
	 * and package should be small enough to send audio streaming as soon as possible.
	 */
	public static final int PacketSize = 10 * 1024;	
	private static final int maxDestroyTime = 1000;
	
	private static final int MONITOR_INTERVAL = 5000;

	private int m_readTimeout = NativeFunction.ReadTimeout; // read timeout milliseconds
	private DatagramSocket m_srcSocket = null;	//When m_srcSocket is null, cache file will be used.
	
	private String m_srcHeaderFilePath = null;
	private String m_srcCacheFilePath0 = null;
	private String m_srcCacheFilePath1 = null;

	/* some times m_listOutput will be used in ReceiveDataProc before it is created in constructor. */
	private ArrayList<DataOutputIf> m_listOutput = new ArrayList<DataOutputIf>();
	
	private int m_nState = 0;
	
	private Thread m_recvThread = null;
	private Thread m_sendThread = null;
	private Thread m_monitorThread = null;
	private BufferPacket m_bufPacket = null;
	private ArrayList<BufferPacket> bufferPackets = new ArrayList<BufferPacket>();
	private Lock m_lockOutput = new ReentrantLock();
	private Lock m_lockPackets = new ReentrantLock();
	private String m_uriSource = null;
	private Integer sourceType;	//0: udp, 1: file
	private File mCurrentCacheFile = null;
	private long mCurrentCachePos = 0;	//used by comparing the max size of the cache file.
	private boolean bFirstCacheReady = false;	//used by skip the previous data of the cache file.
	private Long dataDispatcherKey;
	private long readCount = 0;
	private long writeCount = 0;
	
	private FileOutputStream dumpFileStream = null;
	private boolean bDumpFile = false;	//default: false. True is set when debug is needed.
	
	public DataDispatcher(DatagramSocket datagramSocket, String uriSource) {
		/*set key in construction*/
		createCount++;
		dataDispatcherKey = createCount;
		
		m_readTimeout = NativeFunction.ReadTimeout;
		bDumpFile = String.valueOf("1").equals(NativeFunction.dumpFile);
		
		m_srcSocket = datagramSocket;
    	if (m_srcSocket != null) {
    		try {
    			m_srcSocket.setSoTimeout(m_readTimeout);
    			
    			if (NativeFunction.socketRecvBufferSize != null) {
    				m_srcSocket.setReceiveBufferSize(NativeFunction.socketRecvBufferSize);
    				logger.info("ReceiveBufferSize=" + m_srcSocket.getReceiveBufferSize());
    			}
    		} catch (SocketException e) {
    			logger.log(Level.SEVERE, e.toString(), e);
    		}
    	}
    	
    	m_uriSource = uriSource;
    	if (m_uriSource.contains(KEY_MEDIAFORMAT + "=" + VALUE_FLV)) {
    		sourceType = SOURCE_TYPE_FILE;
    	} else {
    		sourceType = SOURCE_TYPE_UDP;
    	}
    	
		m_srcHeaderFilePath = GetMediaHeaderPath(uriSource);
		if (m_srcHeaderFilePath != null) {
			m_srcCacheFilePath0 = m_srcHeaderFilePath + "_0";
			m_srcCacheFilePath1 = m_srcHeaderFilePath + "_1";
		}
		
		//used for debug
		createDumpFile();
		
		m_monitorThread = new Thread(monitorProc);
		m_monitorThread.start();
		
		m_recvThread = new Thread(ReceiveDataProc);
		m_recvThread.start();
		
		m_sendThread = new Thread(sendDataProc);
		m_sendThread.start();
	}

	public static String GetMediaHeaderPath(String srcPath) {
		String fullName = null;
		if (srcPath == null) {
			return null;
		}
		
		int index = srcPath.indexOf(KEY_MEDIAFORMAT);
		
		if (index >= 0) {
			index += 12;
			if (srcPath.substring(index, index+3).compareToIgnoreCase("flv") == 0) {
				index = srcPath.indexOf("://");
				index = srcPath.indexOf('/', index+3);
				if (index < 0) {
					index = srcPath.indexOf('\\', index+3);
				}
				if (index < 0) {
					index = srcPath.indexOf('?', index+3);
				}
				fullName = (index < 0) ? srcPath.substring(0) : srcPath.substring(0, index);
				fullName = fullName.replace(':', '_');
				fullName = fullName.replace('/', '_');
				fullName = NativeFunction.strTempCachePath + fullName;
			}
		}
		
		return fullName;
	}
	
	private int readCacheFile(byte[] buf, int offset, int length) throws IOException {
		if(mCurrentCacheFile == null) {
			return 0;
		}
		
		RandomAccessFile raFile = null;
		int len = 0;
		try {
			raFile = new RandomAccessFile(mCurrentCacheFile, "r");
			raFile.seek(mCurrentCachePos);
			len = raFile.read(buf, offset, length);
		} finally {
			if (raFile != null) {
				raFile.close();
			}
		}
		return len;
	}
	
	private File getFirstCache() {
		File currentCache = null;
		File file0 = new File(m_srcCacheFilePath0);
		File file1 = new File(m_srcCacheFilePath1);
		
		//find the cache file at first
		if(file0.exists() && file1.exists()) {
			if(file0.lastModified() > file1.lastModified()) {
				currentCache = file0;
			} else {
				currentCache = file1;
			}
		} else if(file0.exists() && !file1.exists()) {
			currentCache = file0;
		} else if(!file0.exists() && file1.exists()) {
			currentCache = file1;
		}
		
		return currentCache;
	}
	
	private File getCurrentCache() throws Exception {
		File currentCache = null;
		File file0 = new File(m_srcCacheFilePath0);
		File file1 = new File(m_srcCacheFilePath1);
		
		if (mCurrentCacheFile == null) {
			throw new Exception("mCurrentCacheFile is null. Something is wrong.");
		}
		
		if(mCurrentCachePos >= NativeFunction.FileCacheMaxSize) {
			//stream is all read out.
			if(mCurrentCacheFile.equals(file0) && file1.exists() && (file0.lastModified() < file1.lastModified())) {
				currentCache = file1;
			} else if(mCurrentCacheFile.equals(file1) && file0.exists() && (file0.lastModified() > file1.lastModified())) {
				currentCache = file0;
			} else {
				currentCache = mCurrentCacheFile;
			}
		} else {
			currentCache = mCurrentCacheFile;
		}
		
		return currentCache;
	}
	
	private int ReadDataFromCacheFile(byte[] buf, int offset, int length) throws Exception {
		if (buf == null) {
			throw new Exception("buf is null");
		}
		
		if (offset < 0) {
			throw new Exception("offset is wrong: " + offset);
		}
		
		if (length <= 0) {
			throw new Exception("length is wrong: " + length);
		}
		
		if (!bFirstCacheReady) {
			mCurrentCacheFile = getFirstCache();
			if (mCurrentCacheFile != null) {
				/*long fileLength = mCurrentCacheFile.length();
				if(fileLength > PacketSize) {
					mCurrentCachePos = fileLength - PacketSize;
				} else {
					mCurrentCachePos = 0;
				}*/
				mCurrentCachePos = mCurrentCacheFile.length();
				bFirstCacheReady = true;
			}
		} else {
			File oldCache = mCurrentCacheFile;
			mCurrentCacheFile = getCurrentCache();
			if (oldCache != mCurrentCacheFile) {
				// Set cache position 0 when the cache file is changed
				mCurrentCachePos = 0;
			}
		}
		
		int readlenth = 0;
		readlenth = readCacheFile(buf, offset, length);
		if (readlenth < 0) {
			//read the file end if readlenth is -1
			readlenth = 0;
		}
		mCurrentCachePos += readlenth;
		
		return readlenth;
	}
	
	public byte[] appendMediaInfo(byte[] buf) throws Exception {
		if(m_srcHeaderFilePath == null) {
			throw new Exception("mediaHeaderPath is null");
		}
		
		File fileHeader = new File(m_srcHeaderFilePath);
		if (!fileHeader.exists()) {
			throw new Exception("m_srcHeaderFilePath does not exist");
		}
		
		int lenHeader = (int)fileHeader.length(); 
		byte[] headBuffer = new byte[lenHeader];
		FileInputStream streamHeader = null;
		try {
			streamHeader = new FileInputStream(fileHeader);
			lenHeader = streamHeader.read(headBuffer, 0, lenHeader);
		} finally {
			if (streamHeader != null) {
				streamHeader.close();
			}
			streamHeader = null;
			fileHeader = null;
		}
		
    	NativeFunction nf = new NativeFunction();
    	int[] retOffset = {-1, 0};	//{have offset -1 or else 0, first IFrame offset}
    	int adaptRet = nf.AdaptFlvHeaderData(headBuffer, headBuffer.length, buf, 0, buf.length, retOffset);
    	if (adaptRet != 0) {
    		return null;
    	}
    	
    	int ifreamOffset = retOffset[1];
    	logger.info(String.format("AdaptFlvHeaderData: adaptRet=%d, ret[0]=%d, ret[1]=%d", adaptRet, retOffset[0], retOffset[1]));

    	int appendedLen = headBuffer.length + buf.length - ifreamOffset;
    	byte[] appendBuffer = new byte[appendedLen];
    	
    	System.arraycopy(headBuffer, 0, appendBuffer, 0, headBuffer.length);
    	System.arraycopy(buf, ifreamOffset, appendBuffer, headBuffer.length, buf.length - ifreamOffset);
    	
		return appendBuffer;
	}
	
	Runnable monitorProc = new Runnable() {
		 public void run() {
			 while (m_nState == 0) {
				 long initReadCount = readCount;
				 long initWriteCount = writeCount;
				 try {
					Thread.sleep(MONITOR_INTERVAL);
					
					if(initReadCount == readCount) {
						logger.info(String.format("readCount is not changed in %d ms, readCount=%d, dataDispatcherKey=%s", MONITOR_INTERVAL, readCount, dataDispatcherKey));
					}
					
					if(initWriteCount == writeCount) {
						logger.info(String.format("writeCount is not changed in %d ms, writeCount=%d, dataDispatcherKey=%s", MONITOR_INTERVAL, writeCount, dataDispatcherKey));
					}
				} catch (Exception e) {
					logger.log(Level.SEVERE, e.toString(), e);
				}
			 }
		 }
	};
	
    Runnable ReceiveDataProc = new Runnable() {
        public void run() {
        	logger.info("ReceiveDataProc.run begin: dataDispatcherKey=" + dataDispatcherKey);
            // receive data from DatagramPacket
    		byte[] buf = new byte[PacketSize];
    		int count = 0;
    		int msWait = 0;
    		int offset = 0;
    		int len = 0;
    		
    		while (m_nState == 0) {
    			try {
    				if (sourceType == SOURCE_TYPE_FILE) {
						// read data from cache file used by FLV over http
						offset = 0;
						msWait = 0;
						do {
							//read file until data is put in.
							len = ReadDataFromCacheFile(buf, offset, buf.length - offset);
    						if (len == 0) {
    							Thread.sleep(50);
    							msWait += 50;
    							if (msWait > m_readTimeout) {
    								//time out exit loop
        							m_nState = STAT_READ_FILE_TIMEOUT;
        		    				break;
    							}
    						}
						} while (len == 0 && m_nState == 0);
					} else if(m_srcSocket != null) {
						DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
	    				m_srcSocket.receive(datagramPacket);
	    				offset = datagramPacket.getOffset();
	    				len = datagramPacket.getLength();
					} else {
						throw new Exception("wrong case here");
					}
    				
    				readCount += len;
    				
    				//used for debug
    				dumpFile(buf, offset, len);
    				
    				while (len > 0) {
    					if (m_bufPacket == null) {
    						m_bufPacket = new BufferPacket(PacketSize);
    					}
    					
    					count = m_bufPacket.Append(buf, offset, len);
    					if (m_bufPacket.IsReady()) {
    						m_lockPackets.lock();
    						try {
    							bufferPackets.add(m_bufPacket);
    						} catch(Exception e) {
    							logger.log(Level.SEVERE, e.toString(), e);
							} finally {
								m_lockPackets.unlock();
							}
    						
    						//clear m_bufPacket so that it can be create in next loop.
    						m_bufPacket = null;
    					}
    					
    					offset += count;
    					len -= count;
    				}
    			} catch (SocketTimeoutException e) {
    				// timeout to receive source packet
    				logger.log(Level.SEVERE, e.toString(), e);
    				m_nState = STAT_SOCKET_TIMEOUT_EXCEPTION;
    			} catch (IOException e) {
    				// source socket/file is closed, quit!
    				logger.log(Level.SEVERE, e.toString(), e);
    				m_nState = STAT_IO_EXCEPTION;
    			} catch (InterruptedException e) {
    				logger.log(Level.SEVERE, e.toString(), e);
    				Thread.currentThread().interrupt();
    				m_nState = STAT_INTERRUPTED_EXCEPTION;
    			} catch (Exception e) {
    				// other exception, quit!
    				logger.log(Level.SEVERE, e.toString(), e);
    				m_nState = STAT_COMMON_EXCEPTION;
    			}
    		}
    		
    		clearAll();
			logger.info(String.format("ReceiveDataProc.run end: m_nState=0x%04x, readCount=%d, dataDispatcherKey=%s", 
					m_nState, readCount, dataDispatcherKey));
        }
    };
    
    Runnable sendDataProc = new Runnable() {
        public void run() {
        	while (m_nState == 0) {
        		/*clear packet*/
        		BufferPacket bufferPacket = null;
        		
        		/* fetch packet */
        		m_lockPackets.lock();
        		try {
	        		if (!bufferPackets.isEmpty()) {
	        			bufferPacket = bufferPackets.remove(0);
	        		}
        		} catch(Exception e) {
        			logger.log(Level.SEVERE, e.toString(), e);
        		} finally {
        			m_lockPackets.unlock();
        		}
	        	
        		if (bufferPacket != null) {
	        		/*send output*/
	        		m_lockOutput.lock();
	        		try {
	        			/*the process should be quickly as soon as possible.*/
	        			
	        				if (m_listOutput != null && !m_listOutput.isEmpty()) {
			        			for(DataOutputIf dataOutput: m_listOutput) {
									dataOutput.DistributeData(bufferPacket);
								}
			        			writeCount += bufferPacket.bufSize;
	        				}
	        		} catch(Exception e) {
	        			logger.log(Level.SEVERE, e.toString(), e);
	        		} finally {
	        			m_lockOutput.unlock();
	        		}
        		} else {
        			try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						logger.log(Level.SEVERE, e.toString(), e);
						Thread.currentThread().interrupt();
					}
        		}
        	}
        	
        	logger.info(String.format("sendDataProc.run end: m_nState=0x%04x, writeCount=%d, dataDispatcherKey=%s", 
					m_nState, writeCount, dataDispatcherKey));
        }
    };

    public DataOutputIf GetOutputIf(OutputStream outStream) {
		m_lockOutput.lock();
		DataOutputIf outIf = null;
		try {
			DataOutputIf out;
    		Iterator<DataOutputIf> iter = m_listOutput.iterator();
    		while (iter.hasNext()) {
    			out = iter.next();
    			if (out != null && out.GetOutStream() == outStream) {
    				outIf = out;
    				break;
    			}
    		}
		} finally {
			m_lockOutput.unlock();
		}
		
    	return outIf;
    }
    
	public boolean RegisterDataOutput(DataOutputIf out) {
		m_lockOutput.lock();
		boolean bAdd = false;
		
		try {
			bAdd = m_listOutput.add(out);
		} finally {
			m_lockOutput.unlock();
		}
		
		return bAdd;
	}

	public boolean UnregisterDataOutput(DataOutputIf out) {
		m_lockOutput.lock();
		boolean bRemove = false;
		
		try {
			bRemove = m_listOutput.remove(out);
		} finally {
			m_lockOutput.unlock();
		}
		
		return bRemove;
	}
	
	public boolean IsOutputEmpty() {
		m_lockOutput.lock();
		boolean bIsEmpty = false;
		
		try {
			bIsEmpty = m_listOutput.isEmpty();
		} finally {
			m_lockOutput.unlock();
		}
		
		return bIsEmpty;
	}
	
	public void Destroy() {
		//destroy should be called as soon as possible.
		m_nState = STAT_BE_DESTROYED;
		logger.info("Destroy end: dataDispatcherKey=" + dataDispatcherKey);
	}
	
	private void clearAll() {
		m_lockOutput.lock();
		try {
			if (m_listOutput != null && !m_listOutput.isEmpty()) {
    			for(DataOutputIf dataOutput: m_listOutput) {
    				dataOutput.stop();
				}
			}
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		} finally {
			m_lockOutput.unlock();
		}
		
		if (m_srcSocket != null) {
			logger.info("clearAll: closing m_srcSocket=" + m_srcSocket.toString());
			m_srcSocket.close();
			m_srcSocket = null;
		}
		
		//used for debug
		closeDumpFile();
	}
	
	private void createDumpFile() {
		if (!bDumpFile) {
			return;
		}
		
		try {
			String dumpFilePath = "/mnt/data/local-disk1/";
			String dumpFileName = "DataDispatcherDump_" + dataDispatcherKey;
			File dumpFile = new File(dumpFilePath, dumpFileName);
			dumpFile.createNewFile();
			dumpFileStream = new FileOutputStream(dumpFile);
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}
	
	private void dumpFile(byte[] buf, int offset, int length) {
		if (!bDumpFile) {
			return;
		}
		
		try {
			dumpFileStream.write(buf, offset, length);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}
	
	private void closeDumpFile() {
		if (!bDumpFile) {
			return;
		}
		
		if(dumpFileStream != null) {
			try {
				dumpFileStream.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.toString(), e);
			}
		}
	}
}
