package edu.kit.fallob.mallobio.output.distributors;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import edu.kit.fallob.mallobio.outputupdates.ResultAvailableObject;

public class ResultObjectDistributorTests {


	
	
	public static final String TEST_RESULT_PATH = "test-log-line";
	
	public static final ResultAvailableObject TEST_RAO = new ResultAvailableObject(TEST_RESULT_PATH);
	
	public static ResultAvailableObject[] TEST_RESULTS;
	
	public static final int AMOUNT_RESULT_TESTS = 10;

	
	private static ResultObjectDistributor dist;
	private static TestResultListener listener;
	
	

	@BeforeEach
	public void setupBeforeEach() {
		dist = new ResultObjectDistributor();
		listener = new TestResultListener();
		dist.addListener(listener);
	}

	@Test
	public void testSingleLogLineDistribution() {
	
		//distribute log line 
		dist.distributeResultObject(TEST_RAO);
		
		//only one log-line should have been processed
		assertTrue(listener.procesedResultObejcts.size() == 1);
		assertTrue(listener.procesedResultObejcts.get(0).equals(TEST_RAO));
	}
	
	
	@Test
	public void testMultipleLogLineDistribution() {
		for (int i = 0; i < AMOUNT_RESULT_TESTS; i++) {
			dist.distributeResultObject(new ResultAvailableObject(TEST_RESULT_PATH));
		}
		assertTrue(listener.procesedResultObejcts.size() == AMOUNT_RESULT_TESTS);
	}
	
	
	@Test
	public void testDistributionAfterRemoval() {
		//distribute log line 
		dist.distributeResultObject(TEST_RAO);
		
		//only one log-line should have been processed
		assertTrue(listener.procesedResultObejcts.size() == 1);
		
		dist.removeListener(listener);
		dist.distributeResultObject(new ResultAvailableObject(TEST_RESULT_PATH));
		
		//no further result-object should have been pushed
		assertTrue(listener.procesedResultObejcts.size() == 1);
	}
}
