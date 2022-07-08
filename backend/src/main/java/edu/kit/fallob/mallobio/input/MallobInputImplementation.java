package edu.kit.fallob.mallobio.input;

public class MallobInputImplementation implements MallobInput {
	
	
	
	private String pathToMallobDirectory;
	
	private int[] clientProcessIDs;
	
	
	//these counters implement round-robin scheduling, such that all requests are uniformly distributed amongst all client-processes
	private int lastUsedClientProcesSubmit = 0;
	private int lastUsedClientProcessAbort = 0;
	
	/**
	 * 
	 * @param pathToMallobDirectory base-directory of mallob-input-directory
	 * @param clientProcessIDs all process-id's of those processes of mallob, that are client-processes
	 */
	public MallobInputImplementation(String pathToMallobDirectory, int[] clientProcessIDs) {
		this.pathToMallobDirectory = pathToMallobDirectory;
		this.clientProcessIDs = clientProcessIDs;
	}

	@Override
	public boolean abortJob(int runningJobID) {
		return false;
	}

}
