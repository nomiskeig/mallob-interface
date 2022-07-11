package edu.kit.fallob.mallobio.input;

import java.nio.file.Files;

import org.json.JSONObject;

import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.dataobjects.MallobAttributeNames;

public class MallobInputImplementation implements MallobInput {
	
	





	private String pathToMallobDirectory;
	
	private String pathToMallobSubmitDirectory;
	private String pathToMallobAbortDirectory;

	
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
	
	/**
	 * Generates the path to a certain client process' in-directory
	 * @param clientProcessID
	 * @return
	 */
	private String generatePathToMallobSubmitDirectory(int clientProcessID) {
		return pathToMallobDirectory += ".api/jobs." + clientProcessID + "/in/";
	}

	@Override
	public boolean abortJob(int runningJobID) {
		return false;
	}

	@Override
	public void submitJobToMallob(String userName, 
			JobConfiguration jobConfiguration, 
			JobDescription jobDescription) 
	{
		// TODO Auto-generated method stub
		
		//Create JSON
		JSONObject jobJSON = new JSONObject();
		jobJSON.put(MallobAttributeNames.MALLOB_USER, userName);
		jobJSON.put(MallobAttributeNames.MALLOB_JOB_NAME, jobConfiguration.getName());
		
		//add description-paths
		//jobJSON.put("files", )
		
		//add additional parameters
		if (jobConfiguration.getWallClockLimit() != JobConfiguration.NOT_SET) {
			jobJSON.put(MallobAttributeNames.MALLOB_WALLCLOCK_LIMIT, jobConfiguration.getWallClockLimit());
		}
		
		if (jobConfiguration.getCpuLimit() != JobConfiguration.NOT_SET) {
			jobJSON.put(MallobAttributeNames.MALLOB_CPU_LIMIT, jobConfiguration.getCpuLimit());
		}
		
		if (jobConfiguration.getArrival() != JobConfiguration.NOT_SET) {
			jobJSON.put(MallobAttributeNames.MALLOB_ARRIVAL, jobConfiguration.getArrival());
		}
		
		if (jobConfiguration.getMaxDemand() != JobConfiguration.NOT_SET) {
			
		}
		
	}

}
