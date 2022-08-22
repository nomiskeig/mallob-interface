package edu.kit.fallob.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
	 * @throws org.json.JSONException if a value was not found in the .json For required values, see Fallob-API
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

		//get json object for the storage values
		JSONObject storageJson = json.getJSONObject("storage");

		c.setGarbageCollectorInterval(storageJson.getInt("garbageCollectorInterval"));
		c.setJobStorageTime(storageJson.getInt("jobStorageTime"));
		c.setEventStorageTime(storageJson.getInt("eventStorageTime"));
		c.setWarningStorageTime(storageJson.getInt("warningStorageTime"));
		c.setMaxDescriptionStorageSize(storageJson.getInt("maxDescriptionStorageSize"));

		c.setStartTime(LocalDateTime.now(ZoneOffset.UTC));


		//get the json object for the default values
		JSONObject defaultsJson = json.getJSONObject("defaults");

		c.setDefaultJobPriority((float) defaultsJson.getDouble("priority"));
		c.setDefaultWallClockLimit(defaultsJson.getString("wallclockLimit"));
		c.setDefaultContentMode(defaultsJson.getString("contentMode"));

		//get the json object for the path values
		JSONObject pathsJson = json.getJSONObject("paths");

		c.setDescriptionsbasePath(pathsJson.getString("descriptionsBasePath"));
		c.setDatabaseBasePath(pathsJson.getString("databaseBasePath"));
		c.setMallobBasePath(pathsJson.getString("mallobBasePath"));
		c.setResultBasePath(pathsJson.getString("resultBasePath"));

		//get the json object for the database values
		JSONObject databaseJson = json.getJSONObject("database");

		c.setDataBaseUsername(databaseJson.getString("databaseUsername"));
		c.setDatabasePassword(databaseJson.getString("databasePassword"));
		
		//get informations for readers
		JSONObject readerSetup = json.getJSONObject("readerSetup");
		c.setReadingIntervalPerReadingThread(readerSetup.getInt("readingIntervalPerReadingThread"));
		c.setAmountReaderThreads(readerSetup.getInt("amountReaderThreads"));
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
