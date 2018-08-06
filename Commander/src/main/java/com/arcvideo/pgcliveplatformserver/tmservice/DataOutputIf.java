package com.arcvideo.pgcliveplatformserver.tmservice;

import java.io.OutputStream;

abstract interface DataOutputIf {
	public OutputStream GetOutStream();
	public int DistributeData(BufferPacket bufPacket);
	public long sendoutProcedureAsync();
	
	/*Used to stop loop*/
	public void stop();
}
