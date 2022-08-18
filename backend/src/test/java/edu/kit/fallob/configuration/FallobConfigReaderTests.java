package edu.kit.fallob.configuration;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FallobConfigReaderTests {
	
	public static final String FILE_PATH = System.getProperty("user.dir") + 
			File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "fallobconfigfile.json";
	
	public static final int AMOUNT_PROCESSES = 14000;
	public static final int MAX_JOBS = 16;
	public static final int MAXJOBSUSER = 1;
	public static final int[] CLIENT_PROCESSES = {3,1,4,5,9};
	public static final int GARBAGECOLLECTINTERVAL = 5000;
	
	
	/*used for job, event and warning_storage time*/
	public static final int STORAGETIME = 20000;
	
	public static final int MAX_DESCRIPTION_SIZE = 20;
	
	public static final double DEFAULTJOBPRIORITY = 0.5;

	public static final String WC_LIMIT = "50s";

	public static final String CONTENT_MODE = "SAT";
	
	public static final String DESCRIPTION_BASE_PATH = "/description";
	
	public static final String DATABASE_BASE_PATH = "/db";

	
	public static final String RESULT_BASE_PATH = "/result";

	
	public static final String MALLOB_BASE_PATH = "/mallob";
	
	public static final String DB_USERNAME = "dbusername";
	
	public static final String DB_PW = "dbpw";
	
	private static String fileContent;
	private static FallobConfiguration fconfig;
	private static FallobConfigReader reader;

	
	public static JSONObject buildJSON() throws JSONException {
		JSONObject o = new JSONObject();
		o.put("amountProcesses", AMOUNT_PROCESSES);
		o.put("maxJobsTotal", MAX_JOBS);
		o.put("maxJobsUser", MAXJOBSUSER);
		o.put("clientProcesses", new JSONArray(CLIENT_PROCESSES));
		o.put("garbageCollectorInterval", GARBAGECOLLECTINTERVAL);
		o.put("jobStorageTime", STORAGETIME);
		o.put("eventStorageTime", STORAGETIME);
		o.put("warningStorageTime", STORAGETIME);
		o.put("maxDescriptionStorageSize", MAX_DESCRIPTION_SIZE);
		o.put("defaultJobPriority", DEFAULTJOBPRIORITY);
		o.put("default-wallclock-limit", WC_LIMIT);
		o.put("defaultContentMode", CONTENT_MODE);
		o.put("descriptionsBasePath", DESCRIPTION_BASE_PATH);
		o.put("mallobBasePath", MALLOB_BASE_PATH);
		o.put("resultBasePath", RESULT_BASE_PATH);
		o.put("databaseBasePath", DATABASE_BASE_PATH);
		o.put("databaseUsername", DB_USERNAME);
		o.put("databasePassword", DB_PW);



		return o;
	}
	
	@Test
	public void testConfigReader() throws IOException {
		try {
		reader = new FallobConfigReader(FILE_PATH);
		
		reader.setupFallobConfig();
		} catch(Exception e) {
			e.printStackTrace();
		}
		fconfig = FallobConfiguration.getInstance();
		//test if attributes match
		assertTrue(fconfig.getAmountProcesses() == AMOUNT_PROCESSES);
		assertTrue(fconfig.getMaxJobsTotal() == MAX_JOBS);
		assertTrue(fconfig.getEventStorageTime() == STORAGETIME 
				&& fconfig.getJobStorageTime() == STORAGETIME && fconfig.getWarningStorageTime() == STORAGETIME);
		
		int[] clientProcesses = fconfig.getClientProcesses();
		for (int i = 0; i < clientProcesses.length; i++) {
			assertTrue(clientProcesses[i] == CLIENT_PROCESSES[i]);
		}
		
		assertTrue(fconfig.getDatabaseBasePath().equals(DATABASE_BASE_PATH));
	}
	
	
	@BeforeAll
	public static void beforeAll() throws IOException, JSONException {
		fileContent = buildJSON().toString();
		new File(FILE_PATH);
		
		//write fileContent to file
		FileWriter writer = new FileWriter(FILE_PATH);
		writer.write(fileContent);
		writer.close();
	}
	
	@AfterAll
	public static void teardown() throws IOException {
		Files.deleteIfExists(Paths.get(FILE_PATH));
	}
}
