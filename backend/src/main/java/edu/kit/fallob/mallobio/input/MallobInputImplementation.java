package edu.kit.fallob.mallobio.input;

import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;

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
	public MallobInputImplementation() {
		FallobConfiguration config = FallobConfiguration.getInstance();
		this.pathToMallobDirectory = config.getMallobBasePath();
		this.clientProcessIDs = config.getClientProcesses();
	}

	@Override
	public boolean abortJob(int runningJobID) {
		return false;
	}

	@Override
	public void submitJobToMallob(String userName, JobConfiguration jobConfiguration, JobDescription jobDescription) {
		// TODO Auto-generated method stub
		
	}

}
