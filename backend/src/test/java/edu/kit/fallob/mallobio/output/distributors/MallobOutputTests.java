package edu.kit.fallob.mallobio.output.distributors;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.fallob.mallobio.outputupdates.ResultAvailableObject;

public class MallobOutputTests {
	
	public static final String TEST_LOG_LINE = "test-log-line";
	public static final ResultAvailableObject TEST_RESULT = new ResultAvailableObject(TEST_LOG_LINE);

	private static OutputLogLineDistributor logDistributor;
	private static ResultObjectDistributor resultDistributor;
	private static MallobOutput output;
	
	private static TestListener logListener;
	private static TestResultListener resultListener;
	
	
	
	
	
	@BeforeEach
	public void setup() {
		logDistributor = new OutputLogLineDistributor();
		resultDistributor = new ResultObjectDistributor();
		output = new MallobOutput(resultDistributor, logDistributor);
		
		logListener = new TestListener();
		resultListener = new TestResultListener();
		

		output.addOutputLogLineListener(logListener);
		output.addResultObjectListener(resultListener);
	}
	
	@Test
	public void testDistributingLogLines() {
		logDistributor.processLogLine(TEST_LOG_LINE);
		assertTrue(logListener.logLines.get(0).equals(TEST_LOG_LINE));
	}
	
	@Test
	public void testDistributingResults() {
		resultDistributor.distributeResultObject(TEST_RESULT);
		assertTrue(resultListener.procesedResultObejcts.get(0).equals(TEST_RESULT));
	}
}
