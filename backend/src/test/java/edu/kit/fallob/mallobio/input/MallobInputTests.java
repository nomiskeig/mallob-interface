package edu.kit.fallob.mallobio.input;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.dataobjects.SubmitType;
import edu.kit.fallob.mallobio.MallobFilePathGenerator;

public class MallobInputTests {
	
	public static final int CLIENT_PROCESS_ID = 0;
	
	public static final String TEST_DIRECTORY_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "inputTests";
	public static final String TEST_MALLOB_IN_DIRECTORY = MallobFilePathGenerator.generatePathToMallobSubmitDirectory(TEST_DIRECTORY_PATH, CLIENT_PROCESS_ID);
	public static final String TEST_DESCRIPTIONFILE = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "testFileDescription.cnf";
	
	
	
	public static final int[] CLIENT_PROCESSES = {CLIENT_PROCESS_ID};
	
	public static final String USERNAME = "admin";

	//necessary job-attributes
	public static final String JOB_NAME = "test-job-1";
	public static final String APPLICATION = "SAT";
	public static final double PRIORITY = 0.7;
	
	//optional attributes 
	public static final String WALLCLOCK_LIMIT = "10m";
	public static final String CPU_LIMIT = "10h";
	public static final double ARRIVAL = 10.3;
	public static final String[] DEPENDENCIES = {"admin.prereq-job1", "admin.prereq-job2"};
	public static final boolean INCREMENTAL = false;
	
	public static File descriptionFile;

	
	
	public static final String TEST_JOB_RESULT = "{\r\n"
			+ "    \"application\": \"SAT\",\r\n"
			+ "    \"user\": \"admin\", \r\n"
			+ "    \"name\": \"test-job-1\", \r\n"
			+ "    \"files\": [\"/path/to/difficult/formula.cnf\"], \r\n"
			+ "    \"priority\": 0.7, \r\n"
			+ "    \"wallclock-limit\": \"5m\", \r\n"
			+ "    \"cpu-limit\": \"10h\",\r\n"
			+ "    \"arrival\": 10.3,\r\n"
			+ "    \"dependencies\": [\"admin.prereq-job1\", \"admin.prereq-job2\"],\r\n"
			+ "    \"incremental\": false\r\n"
			+ "}";
	
	private static MallobInput mInput;
	private static JobConfiguration config;
	private static JobDescription description;
	
	@Test
	public void testJobFileExistance() throws IOException {
		mInput.submitJobToMallob(USERNAME, config, description);
		//assert true that json file is in correct directory
		assertTrue(new File(TEST_MALLOB_IN_DIRECTORY + File.separator + MallobInputImplementation.NEW_JOB_FILENAME + MallobInputImplementation.JSON_FILE_EXTENSION).exists()); 
	}
	
	@Test
	public void testJobFileCorrectness() throws IOException {
		mInput.submitJobToMallob(USERNAME, config, description);
		//assert true that json file is in correct directory
		assertTrue(new File(TEST_MALLOB_IN_DIRECTORY + File.separator + MallobInputImplementation.NEW_JOB_FILENAME + MallobInputImplementation.JSON_FILE_EXTENSION).exists()); 
	}
	
	@BeforeEach
	public void setupBeforeEach() {
		mInput = new MallobInputImplementation(TEST_DIRECTORY_PATH, CLIENT_PROCESSES);
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
		config.setArrival(ARRIVAL);
		config.setDependencies(DEPENDENCIES);
		config.setIncremental(false);
	}
	
	@AfterAll
	public static void afterAll() throws IOException {
		FileUtils.deleteDirectory(new File(TEST_DIRECTORY_PATH + ".api"));
		descriptionFile.delete();
	}
}
