package edu.kit.fallob.mallobio.listeners.outputloglisteners;

public class JobListener implements OutputLogLineListener {
	
	private int mallobID;
	private boolean jobHasFinished;
	
	
	public JobListener(int mallobID) {
		this.mallobID = mallobID;
		this.jobHasFinished = false;
	}

	@Override
	public void processLine(String line) {
		
		synchronized(this) {
			jobHasFinished = true;
			notify();
		}

	}
	
	
	public synchronized void waitForJob() {
		while (!jobHasFinished) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
