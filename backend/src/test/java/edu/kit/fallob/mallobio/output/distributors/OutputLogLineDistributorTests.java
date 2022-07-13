package edu.kit.fallob.mallobio.output.distributors;

import static org.junit.jupiter.api.Assertions.assertTrue;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import edu.kit.fallob.mallobio.output.distributors.OutputLogLineDistributor;

public class OutputLogLineDistributorTests {
	
	
	public static final String TEST_LOG_LINE = "test-log-line";
	
	public static final int AMOUNT_LOG_LINE_TEST = 50;
	
	private static OutputLogLineDistributor dist;
	private static TestListener listener;
	
	
	/**
	 * Create new instances of distributor and listener
	 * add the listener to the distributor
	 */
	@BeforeEach
	public void setupBeforeEach() {
		dist = new OutputLogLineDistributor();
		listener = new TestListener();
		dist.addListener(listener);
	}

	@Test
	public void testSingleLogLineDistribution() {
	
		//distribute log line 
		dist.processLogLine(TEST_LOG_LINE);
		
		//only one log-line should have been processed
		assertTrue(listener.logLines.size() == 1);
		assertTrue(listener.logLines.get(0).equals(TEST_LOG_LINE));
	}
	
	
	@Test
	public void testMultipleLogLineDistribution() {
	
		for (int i = 0; i < AMOUNT_LOG_LINE_TEST; i++) {
			//distribute log line 
			dist.processLogLine(TEST_LOG_LINE);
		}

		
		assertTrue(listener.logLines.size() == AMOUNT_LOG_LINE_TEST);
		
		for (int i = 0; i < AMOUNT_LOG_LINE_TEST; i++) {
			assertTrue(listener.logLines.get(i).equals(TEST_LOG_LINE));
		}
		
	}
	
	@Test
	public void testDistributionAfterRemoval() {
		
		//distribute log line 
		dist.processLogLine(TEST_LOG_LINE);
		
		
		dist.removeListener(listener);
		
		//distribute log line 
		dist.processLogLine(TEST_LOG_LINE);
		
		//if removal was not successful, listener should have gotten two lines by now
		assertTrue(listener.logLines.size() == 1);
	}
}
