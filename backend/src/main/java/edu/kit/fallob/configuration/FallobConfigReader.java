package edu.kit.fallob.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 *
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class FallobConfigReader {
	private String pathToFallobConfigFile;
	
	
	/**
	 * 
	 * @param pathToFallobConfigFile (absolte) filepath to the json-configuration file for fallob
	 * 
	 * @see fallob-api for specification 
	 * @throws FileNotFoundException if file, specified by pathToFallobConfigFile does not exist 
	 */
	public FallobConfigReader(String pathToFallobConfigFile) throws FileNotFoundException {
		this.pathToFallobConfigFile = pathToFallobConfigFile;
		if (!(new File(pathToFallobConfigFile)).exists()){
			throw new FileNotFoundException();
		}
	}
	
	/**
	 * Read config-file and set parameters in FallobConfiguration
	 * 
	 * @throws IOException if reading of file-contents was not successful 
	 */
	public void setupFallobConfig() throws IOException {
		String jsonString = getFileContent();
		FallobConfiguration c = FallobConfiguration.getInstance();
		JSONObject json = new JSONObject(jsonString);
		
		
		c.setMaxJobsTotal(json.getInt("maxJobsTotal"));
		c.setMaxJobsUser(json.getInt("maxJobsUser"));
		
		//int-array is a little bit more complicated
		JSONArray arr = json.getJSONArray("clientProcesses");
		int[] clientProcesses = new int[arr.length()];
		for (int i = 0; i < arr.length(); i++) {
			clientProcesses[i] = arr.getInt(i);
		}
		
		c.setClientProcesses(clientProcesses);
		c.setGarbageCollectorInterval(json.getInt("garbageCollectorInterval"));
		c.setJobStorageTime(json.getInt("jobStorageTime"));
		c.setEventStorageTime(json.getInt("eventStorageTime"));
		c.setWarningStorageTime(json.getInt("warningStorageTime"));
		c.setMaxDescriptionStorageSize(json.getInt("maxDescriptionStorageSize"));
		
		c.setDefaultJobPriority(json.getFloat("defaultJobPriority"));
		
		
		
		c.setDefaultWallClockLimit(json.getString("default-wallclock-limit"));
		c.setDefaultContentMode(json.getString("defaultContentMode"));
		
		c.setDescriptionsbasePath(json.getString("descriptionsbasePath"));
		c.setDatabaseBasePath(json.getString("databaseBasePath"));
		c.setMallobBasePath(json.getString("mallobBasePath"));
		c.setResultBasePath(json.getString("resultBasePath"));
		c.setDataBaseUsername(json.getString("dataBaseUsername"));
		c.setDatabasePassword(json.getString("databasePassword"));
	}

	
	/**
	 * 
	 * @return the file content of the file specified by pathToFallobConfigFile as a string 
	 * @throws IOException 
	 */
	private String getFileContent() throws IOException {
		return Files.readString(Path.of(pathToFallobConfigFile));
	}

}
