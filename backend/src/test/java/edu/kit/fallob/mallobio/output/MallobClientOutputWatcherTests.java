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
	 * Path to a testing directory being created
	 */
	public static final String TEST_DIRECTORY_PATH = System.getProperty("user.dir") + "\\testDirectory";
	public static final String TEST_FILE_NAME = "result";
	public static final String TEST_FILE_EXTENSION = ".txt";
	private static final int AMOUNT_TEST_RESULTS = 10;

	private static int fileCounter = 0;
	
	private static MallobClientOutputWatcher watcher;
	private static TestDistributor testDistributor;
	
	
	private static String createFilePath(int counter) {
		return TEST_DIRECTORY_PATH + "\\" + TEST_FILE_NAME + Integer.toString(counter) + TEST_FILE_EXTENSION;
	}
	
	
	private static void createMockResultFile(int fileCounter) throws IOException {
		File f = new File(createFilePath(fileCounter));
		f.createNewFile();
	}
	
	
	@Test
	public void testFileCreationRecognition() throws IOException {
		createMockResultFile(fileCounter);
		
		assertTrue(testDistributor.resultPaths.size() == 0);
		
		//execute watcher
		watcher.checkForAction();
		
		//check if the watcher distributed correctly 
		
		//check for amount of distributed paths 
		assertTrue(testDistributor.resultPaths.size() == 1);
		//check if file-path is equal to name
		assertTrue(testDistributor.resultPaths.get(0).equals(createFilePath(fileCounter)));
	}
	
	@Test
	public void testFileCreationWithOtherFilesinDirectory() throws IOException {
		//first we want to create a file and let the watcher see it 
		createMockResultFile(fileCounter);
		watcher.checkForAction();
		
		//now, reset the testDistributor
		testDistributor.resetResultPaths();
		
		//now, create another file and let the watcher check for it
		fileCounter++;
		createMockResultFile(fileCounter);
		watcher.checkForAction();
		//distributor should only have one file : because the watcher only pushed one (new file), although 2 are in the directory
		assertTrue(testDistributor.resultPaths.size() == 1);
		assertTrue(testDistributor.resultPaths.get(0).equals(createFilePath(fileCounter)));	
	}
	
	@Test
	public void testMultipleFileCreations() throws IOException {
		for (int i = 0; i < AMOUNT_TEST_RESULTS; i++) {
			createMockResultFile(fileCounter);
			watcher.checkForAction();
			assertTrue(testDistributor.storedResults == (i + 1));
			assertTrue(testDistributor.resultPaths.get(i).equals(createFilePath(fileCounter)));	
			fileCounter++;
		}
	}
	
	@Test
	public void testDirectoryWithFilesInside() throws IOException {
		new File(TEST_DIRECTORY_PATH).mkdirs();
		fileCounter = 0;

		createMockResultFile(fileCounter);
		fileCounter++;
		
		//create instances of watcher and testDistributor
		watcher = new MallobClientOutputWatcher(TEST_DIRECTORY_PATH);
		testDistributor = new TestDistributor();
		watcher.setDistributor(testDistributor);
		
		createMockResultFile(fileCounter);
		watcher.checkForAction();
		
		assertTrue(testDistributor.storedResults == 1);
		assertTrue(testDistributor.resultPaths.get(0).equals(createFilePath(fileCounter)));	

	}
	
	
	@BeforeEach
	public void beforeEach() {
		new File(TEST_DIRECTORY_PATH).mkdirs();
		watcher = new MallobClientOutputWatcher(TEST_DIRECTORY_PATH);
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
