package edu.kit.fallob.mallobio.input;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.dataobjects.MallobAttributeNames;
import edu.kit.fallob.mallobio.MallobFilePathGenerator;

public class MallobInputImplementation implements MallobInput {
	
	

	public static final String NEW_JOB_FILENAME = "newjob";
	
	public static final String JSON_FILE_EXTENSION = ".json";
	
	private String pathToMallobDirectory;
	

	
	private int[] clientProcessIDs;
	private int amountClientProcesses;
	private boolean allProcessesAreClient;
	
	
	//these round-robin counters ensure that all requests are uniformly distributed amongst all client-processes; see getNextProcess()
	private int lastUsedClientProcess = 0;
	
	/**
	 * Standard Constructor for MallobInputImplementation
	 * 
	 * @param pathToMallobDirectory base-directory of mallob-input-directory
	 * @param clientProcessIDs all process-id's of those processes of mallob, that are client-processes
	 */
	public MallobInputImplementation(String pathToMallobDirectory, int[] clientProcessIDs) {
		this.pathToMallobDirectory = pathToMallobDirectory;
		this.clientProcessIDs = clientProcessIDs;
		this.amountClientProcesses = clientProcessIDs.length;
		allProcessesAreClient = false;
	}
	
	
	/**
	 * ONLY USE THIS CONSTRUCTOR, IF ALL PROCESSES ARE CLIENT-PROCESSES 
	 * 
	 * this constructor creates an array a of size amountProcesses. 
	 * 
	 * 
	 * @param pathToMallobDirectory
	 * @param amountProcesses
	 */
	public MallobInputImplementation(String pathToMallobDirectory, int amountProcesses) {
		this.pathToMallobDirectory = pathToMallobDirectory;
		//this.clientProcessIDs = clientProcessIDs;
		allProcessesAreClient = true;
		this.amountClientProcesses = amountProcesses;
	
	}	

	@Override
	public int abortJob(int runningJobID) throws IOException {
		
		int processID = this.getNextProcess();
		
		this.writeJsonInDirectory(createAbortJSON(runningJobID).toString(),
				MallobFilePathGenerator.generatePathToMallobAbortDirectory(pathToMallobDirectory, processID));
				
		return processID;
	}
	

	
	@Override
	public int submitJobToMallob(String userName, 
			JobConfiguration jobConfiguration, 
			JobDescription jobDescription) throws IOException 
	{
		
		int processID = this.getNextProcess();

		String json = createSubmitJSON(userName, jobConfiguration, jobDescription).toString();
		String absoluteFilePath =
				MallobFilePathGenerator.generatePathToMallobSubmitDirectory(pathToMallobDirectory, processID)
				+ File.separator + NEW_JOB_FILENAME + JSON_FILE_EXTENSION;
		
		try {
		this.writeJsonInDirectory(json, absoluteFilePath);
		} catch(Exception e) {
			e.printStackTrace();
		}
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
		
		
		if (jobConfiguration.getArrival() != JobConfiguration.DOUBLE_NOT_SET){
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
		
		json.put(MallobAttributeNames.MALLOB_INCREMENTAL, jobConfiguration.isIncremental());
		
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
		
		if (jobConfiguration.getAdditionalParameter() != JobConfiguration.OBJECT_NOT_SET) {
			addAdditionalParameters(jobConfiguration.getAdditionalParameter(), json);
		}
		

		return json;
	}


	/**
	 * Given a list of strings that each represent a json key-value themselves, each string is first split on the ':' mark.
	 * The first part of the string is then going to be the key, whereas the second part is going to be the value.
	 * 
	 * The method is then going to determine, if the second part is a regular json
	 * @param additionalParameter
	 * @param json
	 */
	private void addAdditionalParameters(List<String> additionalParameter, JSONObject json) {
		for (String s : additionalParameter) {
			String[] parts = s.split(":");
			json.put(parts[0], parts[1]);
		}
	}



	/**
	 * Adds the job-description to a jsonObject. The job-description in the json is going to be a JSONArray of 
	 * file-paths, which are stored in the give jobDescription-Object 
	 * @param jobJSON
	 * @param jobDescription
	 */
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
	
	
	/**
	 * 
	 * This method uses the class-variable lastClientProcess and counts it up, until it reaches the amount of client-
	 * processes. Then it goes back to zero (round robin)
	 * 
	 * If all processes are client processes, the next process-ID is just the counter itself,
	 * if not, the next process-ID is stored in this.clientProcessIDs
	 * 
	 * @return Get the next process-ID for submitting a .json to
	 */
	private int getNextProcess() {
		
		lastUsedClientProcess++;
		if (lastUsedClientProcess >= this.amountClientProcesses) {
			lastUsedClientProcess = 0;
		}
		
		if (allProcessesAreClient) {
			return lastUsedClientProcess;
		}
		return clientProcessIDs[lastUsedClientProcess];
		
	}
}
