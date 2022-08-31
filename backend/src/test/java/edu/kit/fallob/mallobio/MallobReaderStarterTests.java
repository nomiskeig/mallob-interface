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

import edu.kit.fallob.mallobio.output.MallobOutputRunnerThread;



public class MallobReaderStarterTests {
	
	public static final String TEST_DIRECTORY_PATH = System.getProperty("user.dir") + "\\src\\main\\resources\\clientTests";
	public static final String FILE_PATH = System.getProperty("user.dir");

	
	public static final int TEST_AMOUNT_PROCESSES = 100;
	
	public static final int[] TEST_CLIENT_PROCESSES = {2,3,4,5,6,7};
		
	public static final int TEST_AMOUNT_READERTHREADS = 10;
	public static final int TEST_AMOUNT_WATCHERTHREADS = 1;

	
	public static final int TEST_READINGINTERVAL = 100;
	public static final int TEST_WATCHINGINTERVAL = 500;

		
	private static MallobReaderStarter starter;
	
	@Test
	public void testLength() {
		assertTrue(starter.getReaders().length == TEST_AMOUNT_PROCESSES);
		assertTrue(starter.getReaderThreads().length == TEST_AMOUNT_READERTHREADS);
	}
	
	
	@Test
	public void testDistributionOfReaders() {
		MallobOutputRunnerThread[] runners = starter.getReaderThreads();
		assertTrue(runners.length == TEST_AMOUNT_READERTHREADS);

		int lowerBound = (int) Math.floor(TEST_AMOUNT_PROCESSES / TEST_AMOUNT_READERTHREADS);
		int upperBound = lowerBound + 1;
		for (int i = 0; i < runners.length; i++) {
			int amountReadersPerThread = runners[i].getAmountActionCheckers();
			assertTrue(amountReadersPerThread >= lowerBound);
			assertTrue(amountReadersPerThread <= upperBound);
		}
	}
	
	@Test
	public void addIrregularReader() {
		starter.addIrregularReaders(null);
	}


	
	@BeforeEach
	public void setupBeforeEach() {
		starter = new MallobReaderStarter(TEST_DIRECTORY_PATH);
		starter.initOutput(
				TEST_AMOUNT_PROCESSES, 
				TEST_AMOUNT_READERTHREADS, 
				TEST_READINGINTERVAL);
		starter.initInput(TEST_AMOUNT_PROCESSES, TEST_CLIENT_PROCESSES);
		
					
	}

	
}
