package edu.kit.fallob.mallobio.input;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
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
		
		this.writeJsonInDirectory(createAbortJSON(runningJobID).toString(),
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
		String json = createSubmitJSON(userName, jobConfiguration, jobDescription).toString();
		String absoluteFilePath = MallobFilePathGenerator.generatePathToMallobSubmitDirectory(pathToMallobDirectory, 
				clientProcessIDs[lastUsedClientProcesSubmit]) + File.separator + "newjob.json";
		
		
		this.writeJsonInDirectory(json, absoluteFilePath);
		
		int processID = lastUsedClientProcesSubmit;
		updateSubmitCounter();
		return processID;
		
	}
	
	/**
	 * Write the string json into a file specified by path.
	 * Path has to be the absolute file-path 
	 * @param json
	 * @param path
	 * @throws IOException if writing could not be done 
	 */
	private void writeJsonInDirectory(String json, String path) throws IOException {
		File jsonFile = new File(path);
		FileWriter writer = new FileWriter(jsonFile.getAbsolutePath());
		writer.write(json);
		writer.close();
	}
	



	private JSONObject createSubmitJSON(String userName, 
			JobConfiguration jobConfiguration, 
			JobDescription jobDescription) 
	{
		JSONObject json = new JSONObject();
		json.put(MallobAttributeNames.MALLOB_USER, userName);
		json.put(MallobAttributeNames.MALLOB_JOB_NAME, jobConfiguration.getName());
		addJobDescription(json, jobDescription);
		json.put(MallobAttributeNames.MALLOB_PRIORTIY, jobConfiguration.getPriority());
		json.put(MallobAttributeNames.MALLOB_APPLICATION, jobConfiguration.getApplication());		
		
		
		if (jobConfiguration.getWallClockLimit() != JobConfiguration.OBJECT_NOT_SET){
			json.put(MallobAttributeNames.MALLOB_WALLCLOCK_LIMIT, jobConfiguration.getWallClockLimit());
		}
		
		if (jobConfiguration.getCpuLimit() != JobConfiguration.OBJECT_NOT_SET){
			json.put(MallobAttributeNames.MALLOB_CPU_LIMIT, jobConfiguration.getCpuLimit());
		}
		
		
		if (jobConfiguration.getArrival() != JobConfiguration.OBJECT_NOT_SET){
			json.put(MallobAttributeNames.MALLOB_ARRIVAL, jobConfiguration.getArrival());
		}
		
		
		if (jobConfiguration.getMaxDemand() != JobConfiguration.INT_NOT_SET){
			json.put(MallobAttributeNames.MALLOB_MAX_DEMAND, jobConfiguration.getMaxDemand());
		}
	
		if (jobConfiguration.getDependencies() != JobConfiguration.OBJECT_NOT_SET){
			json.put(MallobAttributeNames.MALLOB_DEPENDENCIES, jobConfiguration.getDependencies());
		}
		
		
		if (jobConfiguration.getContentMode() != JobConfiguration.OBJECT_NOT_SET){
			json.put(MallobAttributeNames.MALLOB_CONTENT_MODE, jobConfiguration.getContentMode());
		}
		
		if (jobConfiguration.getDependencies() != JobConfiguration.OBJECT_NOT_SET){
			json.put(MallobAttributeNames.MALLOB_DEPENDENCIES, new JSONArray(jobConfiguration.getDependencies()));
		}
		
		if (jobConfiguration.getLiterals() != JobConfiguration.OBJECT_NOT_SET){
			json.put(MallobAttributeNames.MALLOB_LITERALS, new JSONArray(jobConfiguration.getLiterals()));
		}
		
		if (jobConfiguration.getPrecursor() != JobConfiguration.INT_NOT_SET){
			json.put(MallobAttributeNames.MALLOB_PRECURSOR, jobConfiguration.getPrecursor());
		}
	
		if (jobConfiguration.getAssumptions() != JobConfiguration.OBJECT_NOT_SET){
			json.put(MallobAttributeNames.MALLOB_ASSUMPTIONS, new JSONArray(jobConfiguration.getAssumptions()));
		}
		
		if (jobConfiguration.isDone() != JobConfiguration.BOOL_DEFAULT){
			json.put(MallobAttributeNames.MALLOB_DONE, jobConfiguration.isDone());
		}
		

		//add additional parameters
		//TODO
		return json;
	}


	
	private void addJobDescription(JSONObject jobJSON, JobDescription jobDescription) {
		String[] descriptionPaths = new String[jobDescription.getDescriptionFiles().size()];
		for (int i = 0; i < descriptionPaths.length; i++) {
			descriptionPaths[i] = jobDescription.getDescriptionFiles().get(i).getAbsolutePath();
		}
		jobJSON.put(MallobAttributeNames.MALLOB_DESCRIPTION, new JSONArray(descriptionPaths));
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
