package edu.kit.fallob.mallobio;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.fallob.mallobio.output.distributors.MallobOutput;



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
	public void testInitializationMallobOutput() {
		assertTrue(MallobOutput.getInstance() != null);
	}

	@BeforeEach
	public void setupBeforeEach() {
		starter = new MallobReaderStarter(TEST_DIRECTORY_PATH);
		starter.initOutput(
				TEST_AMOUNT_PROCESSES);
		starter.initInput(TEST_AMOUNT_PROCESSES, TEST_CLIENT_PROCESSES);
		
					
	}
	
}
