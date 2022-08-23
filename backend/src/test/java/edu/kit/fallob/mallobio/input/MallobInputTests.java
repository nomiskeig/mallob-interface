package edu.kit.fallob.mallobio.input;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.dataobjects.MallobAttributeNames;
import edu.kit.fallob.dataobjects.SubmitType;
import edu.kit.fallob.mallobio.MallobFilePathGenerator;

public class MallobInputTests {
	
	public static final int CLIENT_PROCESS_ID = 0;
	
	public static final String TEST_DIRECTORY_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "inputTests";
	public static final String TEST_MALLOB_IN_DIRECTORY = MallobFilePathGenerator.generatePathToMallobSubmitDirectory(TEST_DIRECTORY_PATH, CLIENT_PROCESS_ID);
	public static final String TEST_DESCRIPTIONFILE = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "testFileDescription.cnf";
	
	public static final String ABSOLUTE_SUBMIT_FILE_PATH = TEST_MALLOB_IN_DIRECTORY + File.separator + MallobInputImplementation.NEW_JOB_FILENAME + MallobInputImplementation.JSON_FILE_EXTENSION;
	public static final String ABSOLUTE_ABORT_FILE_PATH = TEST_MALLOB_IN_DIRECTORY + File.separator + MallobInputImplementation.ABORT_FILENAME + MallobInputImplementation.JSON_FILE_EXTENSION;

	
	public static final int[] CLIENT_PROCESSES = {CLIENT_PROCESS_ID};
	
	public static final String USERNAME = "admin";

	//necessary job-attributes
	public static final String JOB_NAME = "test-job-1";
	public static final String APPLICATION = "SAT";
	public static final double PRIORITY = 0.7;
	
	//optional attributes 
	public static final String WALLCLOCK_LIMIT = "10m";
	public static final String CPU_LIMIT = "10h";
	public static final String ARRIVAL = "2022-08-23T11:32:01.840Z";
	public static final String[] DEPENDENCIES = {"admin.prereq-job1", "admin.prereq-job2"};
	public static final boolean INCREMENTAL = false;
	
	public static final int MAX_DEMAND = 5;
	public static final String CONTENT_MODE = "raw"; //if this is in config, file will be read as binary
	
	
	public static final String ADDITIONAL_PARAMETERS = "\"additionalParameters\":{\r\n"
			+ "        \"additionalParameter1\" : \"String\",\r\n"
			+ "        \"additionalParameter2\" : 2,\r\n"
			+ "        \"additionalParameter3\" : [\"Array\", \"von\", \"Strings\"]\r\n"
			+ "    }";
	
	
	public static File descriptionFile;

	
	//this is how the contents of the json file should look like after generation by mallobinput
	public static String TEST_JOB; 
	
	
	
	private static MallobInput mInput;
	private static JobConfiguration config;
	private static JobDescription description;
	
	@Test
	public void testJobFileExistanceSubmit() throws IOException {
		mInput.submitJobToMallob(USERNAME, config, description);
		//assert true that json file is in correct directory
		assertTrue(new File(ABSOLUTE_SUBMIT_FILE_PATH).exists()); 
	}
	
	
	@Test
	public void testJobFileExistanceAbort() throws IOException {
		
		mInput.abortJob(USERNAME, JOB_NAME);
		String expectedJSONContent = 
				"{\""  + MallobAttributeNames.MALLOB_JOB_NAME + "\":\"" + JOB_NAME + "\","
				+ "\"" + MallobAttributeNames.MALLOB_INTERRUPT + "\":true,"
				+ "\"" + MallobAttributeNames.MALLOB_USER + "\":\"" + USERNAME + "\"}";
		
		
		String jsonContent = Files.readString(Path.of(ABSOLUTE_ABORT_FILE_PATH));
		assertTrue(jsonContent.equals(expectedJSONContent));
		assertTrue(new File(ABSOLUTE_ABORT_FILE_PATH).exists()); 
	}
	
	@Test
	public void testJobFileCorrectness() throws IOException {
		/*
		mInput.submitJobToMallob(USERNAME, config, description);	
		String fileContent = Files.readString(Path.of(ABSOLUTE_FILE_PATH));
		System.out.println("File COntent : " + fileContent);
		System.out.println("Controlstring :" + TEST_JOB);
		assertTrue(fileContent.equals(TEST_JOB));
		*/
	}
	
	
	
	@BeforeEach
	public void setupBeforeEach() {
		mInput = MallobInputImplementation.getInstance();
		((MallobInputImplementation) mInput).setupInput(TEST_DIRECTORY_PATH, CLIENT_PROCESSES);
	}
	
	
	@BeforeAll
	public static void beforeAll() {
		new File(TEST_MALLOB_IN_DIRECTORY).mkdirs();
		
		//setup config
		//config = new JobConfiguration(JOB_NAME, PRIORITY, APPLICATION, -1, WALLCLOCK_LIMIT, 0, 0, null, false, 0, 0, null);
		config = new JobConfiguration(JOB_NAME, PRIORITY, APPLICATION);
		
		descriptionFile = new File(TEST_DESCRIPTIONFILE);
		description = new JobDescription(List.of(descriptionFile), SubmitType.EXCLUSIVE);
		
		config.setWallClockLimit(WALLCLOCK_LIMIT);
		config.setCpuLimit(CPU_LIMIT);
		config.setArrival(null);
		config.setDependenciesStrings(DEPENDENCIES);
		config.setIncremental(false);
		
		TEST_JOB = "{\"application\":\"SAT\","
				+ "\"arrival\":10.3,"
				+ "\"wallclock-limit\":\"10m\","
				+ "\"cpu-limit\":\"10h\","
				+ "\"name\":\"test-job-1\","
				+ "\"files\":["+TEST_DESCRIPTIONFILE+"],"
				+ "\"incremental\":false,\"priority\":0.7,"
				+ "\"user\":\"admin\","
				+ "\"dependencies\":[\"admin.prereq-job1\","
				+ "\"admin.prereq-job2\"]}";
	}
	
	@AfterAll
	public static void afterAll() throws IOException {
		FileUtils.deleteDirectory(new File(TEST_DIRECTORY_PATH + ".api"));
		descriptionFile.delete();
	}
}
