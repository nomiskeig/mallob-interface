package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import java.util.ArrayList;
import java.util.List;

public class PeriodicBufferChecker extends Thread {
	
	
	private static PeriodicBufferChecker pbc;
	
	public static PeriodicBufferChecker getInstance() {
		if (pbc == null) {
			pbc = new PeriodicBufferChecker();
		}
		return pbc;
	}
	
	
	private int interval;
	private boolean forceRetry;
	private List<Buffer<?>> buffers;
	
	private PeriodicBufferChecker() {
		//this.interval = FallobConfigurations.getInstance().getBufferRetryInterval();
		buffers = new ArrayList<>();
		forceRetry = true;
	}
	
	public void addBuffer(Buffer<?> buffer) {
		this.buffers.add(buffer);
	}
	
	

	@Override
	public void run() {
		while (forceRetry) {
			for (Buffer<?> buffer : buffers) {
				buffer.retryBufferedFunction();
			}
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				System.out.println(" ERROR : PeriodicBufferChecker got interrupted while sleeping ");
			}
		}
	}

	
	
}
