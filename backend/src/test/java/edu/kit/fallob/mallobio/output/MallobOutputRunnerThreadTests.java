package edu.kit.fallob.mallobio.output;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MallobOutputRunnerThreadTests {
	


	private static MallobOutputRunnerThread testThread;
	
	private static final int TEST_ACTION_CHECK_INTERVAL = 1000;

	
	/**
	 * Tests, if the amount of action checkers is counted correctly 
	 */
	@Test
	public void actionCheckerCounterTest() {
		testThread.addActionChecker(new TestActionChecker());
		assertTrue(testThread.getAmountActionCheckers() == 1);
	}
	
	
	
	@Test
	public void actionCheckerRemovalTest() throws InterruptedException {
		TestActionChecker checker = new TestActionChecker();
		testThread.addActionChecker(checker);
		assertTrue(testThread.getAmountActionCheckers() == 1);
		
		
		checker.isDone = true;
		testThread.executeRunIteration();
		assertTrue(testThread.getAmountActionCheckers() == 0);
	}

	@BeforeEach
	public void beforeEach() {
		testThread = new MallobOutputRunnerThread(TEST_ACTION_CHECK_INTERVAL);
	}
}
