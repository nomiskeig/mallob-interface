package edu.kit.fallob.mallobio;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.fallob.mallobio.output.MallobFilePathGenerator;



public class MallobReaderStarterTests {
	
	public static final String TEST_MALLOB_API_PATH = System.getProperty("user.dir") + "/mallobTestAPI";
	public static final String FILE_PATH = System.getProperty("user.dir");

	
	public static final int TEST_AMOUNT_PROCESSES = 100;
		
	public static final int TEST_AMOUNT_READERTHREADS = 10;
	public static final int TEST_AMOUNT_WATCHERTHREADS = 1;

	
	public static final int TEST_READINGINTERVAL = 100;
	public static final int TEST_WATCHINGINTERVAL = 500;

		
	private static MallobReaderStarter starter;
	
	@Test
	public void testInitializationMallobOutput() {
		assertTrue(starter.getMallobOutput() != null);
	}
	
	/*
	@Test
	public void threadCreationAndStoppage() throws InterruptedException {
		
		int amountThreadsBeforeCreation = Thread.activeCount();
		starter.startMallobio();
		assertTrue(Thread.activeCount() == amountThreadsBeforeCreation + TEST_AMOUNT_READERTHREADS + TEST_AMOUNT_WATCHERTHREADS);
		
		starter.stopMallobio();
		assertTrue(Thread.activeCount() == amountThreadsBeforeCreation);
		
	}
	*/

	
	@BeforeEach
	public void setupBeforeEach() {
		starter = new MallobReaderStarter();
		starter.initParsingModule(TEST_MALLOB_API_PATH, 
				TEST_AMOUNT_PROCESSES, 
				TEST_AMOUNT_WATCHERTHREADS, 
				TEST_WATCHINGINTERVAL, 
				TEST_AMOUNT_READERTHREADS, 
				TEST_READINGINTERVAL);
		
					
	}

	@BeforeAll
	public static void setupFiles() {
		/*
		//create directory
		new File(TEST_MALLOB_API_PATH).mkdirs();
		
		//Create directory of logs 
		for (int i = 0; i < TEST_AMOUNT_PROCESSES; i++) {
			new File(MallobFilePathGenerator.generateLogDirectoryPath(i, TEST_MALLOB_API_PATH)).mkdirs();
			new File(MallobFilePathGenerator.generateLogFilePath(i, TEST_MALLOB_API_PATH));
		}
		*/
	}
	
	@AfterAll
	public static void deleteFile() throws IOException {
		//FileUtils.deleteDirectory(new File(TEST_MALLOB_API_PATH));
	}
	
	
}
