package edu.kit.fallob.mallobio.input;


import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;


import edu.kit.fallob.dataobjects.MallobAttributeNames;


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
	
	/**
	 * Generates the path to a certain client process' in-directory
	 * @param clientProcessID
	 * @return
	 */
	private String generatePathToMallobSubmitDirectory(int clientProcessID) {
		return pathToMallobDirectory += ".api/jobs." + clientProcessID + "/in/";
	}
	
	/**
	 * Generates the path to a certain client process' in-directory
	 * @param clientProcessID
	 * @return
	 */
	private String generatePathToMallobAbortDirectory(int clientProcessID) {
		return pathToMallobDirectory += ".api/jobs." + clientProcessID + "/in/";
	}

	@Override
	public void abortJob(int runningJobID) throws IOException {
		
		this.writeJsonInDirectory(createAbortJSON(runningJobID),
				this.generatePathToMallobAbortDirectory(clientProcessIDs[lastUsedClientProcessAbort]));
		updateAbortCounter();
	}

	
	@Override
	public void submitJobToMallob(String userName, 
			JobConfiguration jobConfiguration, 
			JobDescription jobDescription) throws IOException 
	{
		this.writeJsonInDirectory(createSubmitJSON(userName, jobConfiguration, jobDescription), 
				generatePathToMallobSubmitDirectory(clientProcessIDs[lastUsedClientProcesSubmit]));
		updateSubmitCounter();
	}
	
	private void writeJsonInDirectory(JSONObject json, String path) throws IOException {
		File jsonFile = new File(path);
		FileWriter writer = new FileWriter(jsonFile.getAbsolutePath());
		writer.write(json.toString());
		writer.close();
	}
	



	private JSONObject createSubmitJSON(String userName, 
			JobConfiguration jobConfiguration, 
			JobDescription jobDescription) 
	{
		JSONObject jobJSON = new JSONObject();
		jobJSON.put(MallobAttributeNames.MALLOB_USER, userName);
		jobJSON.put(MallobAttributeNames.MALLOB_JOB_NAME, jobConfiguration.getName());
		
		//add description-paths
		//jobJSON.put("files", )
		
		addJobDescription(jobJSON, jobDescription);
		
		//add additional parameters
		if (jobConfiguration.getWallClockLimit() != null) {
			jobJSON.put(MallobAttributeNames.MALLOB_WALLCLOCK_LIMIT, jobConfiguration.getWallClockLimit());
		}
		
		if (jobConfiguration.getCpuLimit() != null) {
			jobJSON.put(MallobAttributeNames.MALLOB_CPU_LIMIT, jobConfiguration.getCpuLimit());
		}
		
		if (jobConfiguration.getArrival() != null) {
			jobJSON.put(MallobAttributeNames.MALLOB_ARRIVAL, jobConfiguration.getArrival());
		}
		
		if (jobConfiguration.getMaxDemand() != JobConfiguration.NOT_SET) {
			jobJSON.put(MallobAttributeNames.MALLOB_MAX_DEMAND, jobConfiguration.getMaxDemand());
		}
		
		return jobJSON;
	}


	private void addJobDescription(JSONObject jobJSON, JobDescription jobDescription) {
		
	}
	
	
	private JSONObject createAbortJSON(int runningJobID) {
		return null;
	}
	
	
	
	private void updateSubmitCounter() {
		lastUsedClientProcesSubmit++;
		if (lastUsedClientProcesSubmit >= clientProcessIDs.length) {
			lastUsedClientProcesSubmit = 0;
		}
	}
	
	private void updateAbortCounter() {
		lastUsedClientProcessAbort++;
		if (lastUsedClientProcessAbort >= clientProcessIDs.length) {
			lastUsedClientProcessAbort = 0;
		}
	}

	

}
