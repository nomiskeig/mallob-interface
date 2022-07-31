package edu.kit.fallob.mallobio.input;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;

import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.dataobjects.MallobAttributeNames;
import edu.kit.fallob.mallobio.MallobFilePathGenerator;

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
	public int abortJob(int runningJobID) throws IOException {
		
		this.writeJsonInDirectory(createAbortJSON(runningJobID),
				MallobFilePathGenerator.generatePathToMallobAbortDirectory(pathToMallobDirectory, 
						clientProcessIDs[lastUsedClientProcessAbort]));
		
		
		int processID = lastUsedClientProcessAbort;
		updateAbortCounter();
		return processID;
	}

	
	@Override
	public int submitJobToMallob(String userName, 
			JobConfiguration jobConfiguration, 
			JobDescription jobDescription) throws IOException 
	{
		this.writeJsonInDirectory(createSubmitJSON(userName, jobConfiguration, jobDescription), 
				MallobFilePathGenerator.generatePathToMallobSubmitDirectory(pathToMallobDirectory, clientProcessIDs[lastUsedClientProcesSubmit]));
		
		int processID = lastUsedClientProcesSubmit;
		updateSubmitCounter();
		return processID;
		
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
		//TODO
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
