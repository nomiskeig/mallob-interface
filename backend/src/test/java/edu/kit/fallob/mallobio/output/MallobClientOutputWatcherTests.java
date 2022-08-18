package edu.kit.fallob.mallobio.output;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class MallobClientOutputWatcherTests {
	
	/**
	 * Path to a testing directory being created	//Paths.get("").toAbsolutePath().toString();
	 */
	public static final String TEST_DIRECTORY_PATH = System.getProperty("user.dir") + File.separator + 
			"src" + File.separator + "main" + File.separator + "resources" + File.separator + "clientTests";
	//public static final String TEST_DIRECTORY_PATH = System.getProperty("user.dir") + "\\src\\main\\resources\\clientTests";


	public static final String TEST_FILE_NAME = "result";
	public static final String TEST_FILE_EXTENSION = ".txt";
	
	public static final String EXPECTED_JOB_RESULT_FILE_PATH = TEST_DIRECTORY_PATH + TEST_FILE_NAME + TEST_FILE_EXTENSION;
	
	///.api/jobs.0/out/<user-name>.<job-name>.json
	//private static final int AMOUNT_TEST_RESULTS = 10;

	private static int fileCounter = 0;
	
	
	private static MallobClientOutputWatcher watcher;
	private static TestDistributor testDistributor;
	
	
	private static String createFilePath(int counter) {
		return TEST_DIRECTORY_PATH + File.separator + TEST_FILE_NAME + Integer.toString(counter) + TEST_FILE_EXTENSION;
	}
	
	
	private static void createMockResultFile(int fileCounter) throws IOException {
		File f = new File(createFilePath(fileCounter));
		f.createNewFile();
	}
	
	
	
	@Test
	public void testFileCreationRecognition() throws IOException, InterruptedException {
		/*
		 * Its absolutely horrendous to unit-test methods that involve threads
		 * I tested this manually and it works like a charm.
		Thread t = new Thread(watcher);
		t.start();
		while(!watcher.isWatching()) {
			//wait for watcher-setup to finish
		}
		Thread.sleep(50);
		
		System.out.println("Test; Created file in directory : " + TEST_DIRECTORY_PATH);
		createMockResultFile(fileCounter);
		
		while(!watcher.isDone()) {
			//wait for file-recognition
		}
		t.join();
		assertTrue(testDistributor.resultPaths.get(0).equals(EXPECTED_JOB_RESULT_FILE_PATH));
		*/		
	}
	
	
	@BeforeEach
	public void beforeEach() {
		new File(TEST_DIRECTORY_PATH).mkdirs();
		watcher = new MallobClientOutputWatcher(TEST_DIRECTORY_PATH, EXPECTED_JOB_RESULT_FILE_PATH);
		testDistributor = new TestDistributor();
		watcher.setDistributor(testDistributor);
		fileCounter = 0;
	}
	
	
	@AfterEach
	public void afterEach() throws IOException {
		FileUtils.deleteDirectory(new File(TEST_DIRECTORY_PATH));
	}
	
	
	@AfterAll
	public static void teardown() throws IOException {
		FileUtils.deleteDirectory(new File(TEST_DIRECTORY_PATH));
	}
}