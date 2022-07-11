package edu.kit.fallob.mallobio.output;

import java.util.List;

/**
 * 
 * @author Simon Wilhelm Schübel
 *
 */
public class MallobOutputReaderRunner implements Runnable {
	
	private int readInterval;
	private List<MallobOutputReader> mors;
	
	
	public MallobOutputReaderRunner(int readInterval) {
		this.readInterval = readInterval;
	}

	public void addMallobOutputReader(MallobOutputReader mor) {
		this.mors.add(mor);
	}
	
	public void runMallobOutputreaders() {
		
	}

	
	/**
	 * First, creates all the MallobOutputReaders and maps them to 
	 * a thread
	 */
	private void createReaders() {
		
	}
	
	private void runReaders() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
