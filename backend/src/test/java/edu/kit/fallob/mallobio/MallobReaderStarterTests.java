package edu.kit.fallob.mallobio;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class MallobReaderStarterTests {
	
	
	public static final String TEST_MALLOB_API_PATH = null;
	
	public static final int TEST_AMOUNT_PROCESSES = 100;
	
	public static final int[] TEST_CLIENT_PROCESSES = {0, 10, 14};
	
	public static final int TEST_AMOUNT_READERTHREADS = 10;
	
	public static final int TEST_READINGINTERVAL = 100;
	


	
	
	
	private static MallobReaderStarter starter;
	
	@Test
	public void testInitializationMallobOutput() {
		starter.initParsingModule(TEST_MALLOB_API_PATH, 
				TEST_CLIENT_PROCESSES, 
				TEST_AMOUNT_PROCESSES, 
				TEST_AMOUNT_READERTHREADS, 
				TEST_READINGINTERVAL);
		
		assertTrue(starter.getMallobOutput() != null);
	}
	
	
	@BeforeEach
	public static void setupBeforeEach() {
		starter = new MallobReaderStarter();
	}

	@BeforeAll
	public static void setup() {
		
	}
	
	@AfterAll
	public static void teardown() {
	}
	
}
