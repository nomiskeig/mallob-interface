package edu.kit.fallob.mallobio.input;


import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


import edu.kit.fallob.dataobjects.MallobAttributeNames;
import edu.kit.fallob.mallobio.MallobFilePathGenerator;


public class MallobInputImplementation implements MallobInput {
	
	private static MallobInputImplementation mii;
	
	

	public static final String NEW_JOB_FILENAME = "newjob";
	public static final String NEW_JOB_FILENAME_TILDE = "~newjob";
	
	public static final String ABORT_FILENAME = "abortjob";
	public static final String ABORT_FILENAME_TILDE = "~abortjob";
	
	public static final String JSON_FILE_EXTENSION = ".json";
	
	private String pathToMallobDirectory;
	

	
	private int[] clientProcessIDs;
	private int amountClientProcesses;
	private boolean allProcessesAreClient;
	private int fileCounter;
	
	//these round-robin counters ensure that all requests are uniformly distributed amongst all client-processes; see getNextProcess()
	private int lastUsedClientProcess = 0;
	private boolean useTilde = false;
	
	public static MallobInputImplementation getInstance() {
		if (mii == null) {
			mii = new MallobInputImplementation();
		}
		return mii;
	}
	
	private MallobInputImplementation() {fileCounter = 0;};
	
	
	/**
	 * Standard setup for MallobInputImplementation
	 * 
	 * @param pathToMallobDirectory base-directory of mallob-input-directory
	 * @param clientProcessIDs all process-id's of those processes of mallob, that are client-processes
	 */
	public void setupInput(String pathToMallobDirectory, int[] clientProcessIDs) {
		this.pathToMallobDirectory = pathToMallobDirectory;
		this.clientProcessIDs = clientProcessIDs;
		this.amountClientProcesses = clientProcessIDs.length;
		allProcessesAreClient = false;
	}
	
	
	
	/**
	 * ONLY USE THIS SETUP, IF ALL PROCESSES ARE CLIENT-PROCESSES 
	 * 
	 * this constructor creates an array a of size amountProcesses. 
	 * 
	 * 
	 * @param pathToMallobDirectory
	 * @param amountProcesses
	 */
	public void setupInputAllProcesses(String pathToMallobDirectory, int amountProcesses) {
		this.pathToMallobDirectory = pathToMallobDirectory;
		//this.clientProcessIDs = clientProcessIDs;
		allProcessesAreClient = true;
		this.amountClientProcesses = amountProcesses;
	}

	

	@Override
	public int abortJob(String username, String jobName, boolean isDone) throws IOException {
		
		int processID = this.getNextProcess();
		String abortJson = createAbortJSON(username, jobName, isDone).toString();
		String filePath = MallobFilePathGenerator.generatePathToMallobAbortDirectory(pathToMallobDirectory, processID) 
				+ getFileName(ABORT_FILENAME) + JSON_FILE_EXTENSION;
	
		if (useTilde) {
        	String absoluteFilePathTilde =
    				MallobFilePathGenerator.generatePathToMallobSubmitDirectory(pathToMallobDirectory, processID)
    				+ getFileName(NEW_JOB_FILENAME_TILDE) + JSON_FILE_EXTENSION;

    		this.writeJsonInDirectoryTilde(abortJson, absoluteFilePathTilde, filePath);
    		return processID;
        } else {
    		this.writeJsonInDirectory(abortJson, filePath);
    		return processID;
        }

	}
	

	
	@Override
	public int submitJobToMallob(String userName, 
			JobConfiguration jobConfiguration, 
			JobDescription jobDescription) throws IOException
	{
		
		int processID = this.getNextProcess();
		JSONObject jsonWithStandardParameters = createSubmitJSON(userName, jobConfiguration, jobDescription);
		String json = jsonWithStandardParameters.toString();

		//IF there are additional parameters, the JSONObject library does not support just adding a single string (formatted as a key-value-pair)
		//so this has to be done by manipulating the string
		if (jobConfiguration.getAdditionalParameter() != JobConfiguration.OBJECT_NOT_SET) {
			json = addAdditionalParameters(jobConfiguration.getAdditionalParameter(), json);
		}
        System.out.println("final json:\n" + json);
        
        String absoluteFilePath =
				MallobFilePathGenerator.generatePathToMallobSubmitDirectory(pathToMallobDirectory, processID)
				+ getFileName(NEW_JOB_FILENAME) + JSON_FILE_EXTENSION;

        if (useTilde) {
        	String absoluteFilePathTilde =
    				MallobFilePathGenerator.generatePathToMallobSubmitDirectory(pathToMallobDirectory, processID)
    				+ getFileName(NEW_JOB_FILENAME_TILDE) + JSON_FILE_EXTENSION;

    		this.writeJsonInDirectoryTilde(json, absoluteFilePathTilde, absoluteFilePath);
    		
    		return processID;
        }
        
        else {
    		this.writeJsonInDirectory(json, absoluteFilePath);
    		
    		return processID;
        }

	}
	
	private void writeJsonInDirectoryTilde(String json, String absoluteFilePathTilde, String absoluteFilePath) throws IOException {
		File jsonFile = new File(absoluteFilePathTilde);
		FileWriter writer = new FileWriter(jsonFile.getAbsolutePath());
		writer.write(json);
		writer.close();
		File fileWithoutTilde = new File(absoluteFilePath);
		jsonFile.renameTo(fileWithoutTilde);
	}

	public void useTilde() {
		this.useTilde = true;
	}
	
	public void dontUseTilde() {
		this.useTilde = false;
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
		System.out.println("Wrote file in directory : " + path);
	}
	
	private String getFileName(String fileBaseName) {
		fileCounter++;
		return fileBaseName + Integer.toString(fileCounter);
		
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
		
		double arrivalAsDouble = AbsoluteTimeConverter.convertTimeToDouble(jobConfiguration.getArrival());
		if (arrivalAsDouble != JobConfiguration.DOUBLE_NOT_SET){
			json.put(MallobAttributeNames.MALLOB_ARRIVAL, arrivalAsDouble);
		}
	
		
		if (jobConfiguration.getMaxDemand() != JobConfiguration.INT_NOT_SET){
			json.put(MallobAttributeNames.MALLOB_MAX_DEMAND, jobConfiguration.getMaxDemand());
		}
		
		if (jobConfiguration.getContentMode() != JobConfiguration.OBJECT_NOT_SET){
			json.put(MallobAttributeNames.MALLOB_CONTENT_MODE, jobConfiguration.getContentMode());
		}
		
		if (jobConfiguration.getDependenciesStrings() != JobConfiguration.OBJECT_NOT_SET){
			json.put(MallobAttributeNames.MALLOB_DEPENDENCIES, new JSONArray(jobConfiguration.getDependenciesStrings()));
		}
		
		json.put(MallobAttributeNames.MALLOB_INCREMENTAL, jobConfiguration.isIncremental());
		
		if (jobConfiguration.getLiterals() != JobConfiguration.OBJECT_NOT_SET){
			json.put(MallobAttributeNames.MALLOB_LITERALS, new JSONArray(jobConfiguration.getLiterals()));
		}
		
		if (jobConfiguration.getPrecursor() != JobConfiguration.INT_NOT_SET){
			json.put(MallobAttributeNames.MALLOB_PRECURSOR, jobConfiguration.getPrecursorString());
		}
	
		if (jobConfiguration.getAssumptions() != JobConfiguration.OBJECT_NOT_SET){
			json.put(MallobAttributeNames.MALLOB_ASSUMPTIONS, new JSONArray(jobConfiguration.getAssumptions()));
		}
		
		if (jobConfiguration.isDone() != JobConfiguration.BOOL_DEFAULT){
			json.put(MallobAttributeNames.MALLOB_DONE, jobConfiguration.isDone());
		}
		

		return json;
	}


	/**
	 * This method removes the closing-bracket from the fiven json string and adds a comma, to sperate 
	 * additionalParameters. 
	 * After the comma it adds the additional-parameters and the }.
	 * 
	 * Therefore the additional parameters have to be the last thing added to the json-string.
	 * 
	 * The method is then going to determine, if the second part is a regular json
	 * @param additionalParameter
	 * @param json a string in .json format (so it ends with }, and all other parameters already written.
	 */
	private String addAdditionalParameters(String additionalParameter, String json) {
		//remove json-closing bracket 
		String newJson = json.toString().substring(0, json.length()- 1);
		
        System.out.println("additionalParameter");
        System.out.println(additionalParameter);
		//add additional-parameter tag 
        String trimmedParameter = additionalParameter.substring(1, additionalParameter.length() -1);
		return newJson += "," + trimmedParameter + "}";
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
			descriptionPaths[i] = "./.api/jobs.0/in/descriptions/" +jobDescription.getDescriptionFiles().get(i).getName();
             
		}
		jobJSON.put(MallobAttributeNames.MALLOB_DESCRIPTION, new JSONArray(descriptionPaths));
	}
	
	
	
	private JSONObject createAbortJSON(String username, String jobName, boolean isDone) {
		JSONObject json = new JSONObject();
		json.put(MallobAttributeNames.MALLOB_USER, username);
		json.put(MallobAttributeNames.MALLOB_JOB_NAME, jobName);
		if (isDone) {
			json.put(MallobAttributeNames.MALLOB_DONE, true);
		} else {
			json.put(MallobAttributeNames.MALLOB_INTERRUPT, true);
		}
		return json;
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
