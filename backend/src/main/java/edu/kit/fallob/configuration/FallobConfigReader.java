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
	 * @throws org.json.JSONException if a value was not found in the json. For required values, see Fallob-API
	 */
	public void setupFallobConfig() throws IOException, org.json.JSONException {
		String jsonString = getFileContent();
		FallobConfiguration c = FallobConfiguration.getInstance();
		JSONObject json = new JSONObject(jsonString);
		
		c.setAmountProcesses(json.getInt("amountProcesses"));
		c.setMaxJobsTotal(json.getInt("maxJobsTotal"));
		c.setMaxJobsUser(json.getInt("maxJobsUser"));
		
		//int-array is a little bit more complicated
		JSONArray arr = json.getJSONArray("client-processes");
		int[] clientProcesses = new int[arr.length()];
		for (int i = 0; i < arr.length(); i++) {
			clientProcesses[i] = arr.getInt(i);
		}
		
		c.setClientProcesses(clientProcesses);
		
		JSONObject storage = (JSONObject) json.get("storage");
		c.setGarbageCollectorInterval(storage.getInt("garbageCollectorInterval"));
		c.setJobStorageTime(storage.getInt("jobStorageTime"));
		c.setEventStorageTime(storage.getInt("eventStorageTime"));
		c.setWarningStorageTime(storage.getInt("warningStorageTime"));
		c.setMaxDescriptionStorageSize(storage.getInt("maxDescriptionStorageSize"));
		
		JSONObject defaults = (JSONObject) json.get("defaults");
		c.setDefaultJobPriority((float) defaults.getDouble("priority"));
		c.setDefaultWallClockLimit(defaults.getString("wallclockLimit"));
		c.setDefaultContentMode(defaults.getString("contentMode"));
		
		JSONObject paths = (JSONObject) json.get("paths");
		c.setDescriptionsbasePath(paths.getString("descriptionsBasePath"));
		c.setDatabaseBasePath(paths.getString("databaseBasePath"));
		c.setMallobBasePath(paths.getString("mallobBasePath"));
		c.setResultBasePath(paths.getString("resultBasePath"));
		
		JSONObject database = (JSONObject) json.get("database");
		c.setDataBaseUsername(database.getString("databaseUsername"));
		c.setDatabasePassword(database.getString("databasePassword"));
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
