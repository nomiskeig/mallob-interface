package edu.kit.fallob.mallobio.output;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class MallobOutputRunnerThread implements Runnable {
	
	private List<MallobOutputActionChecker> checkers;
	private int checkActionInterval;
	private boolean runCheckers = true;
	
	public MallobOutputRunnerThread(int checkActionInterval) {
		this.checkActionInterval = checkActionInterval;
		this.checkers = new ArrayList<>();
	}
	
	@Override
	public void run() {
		while(runCheckers) {
			for(MallobOutputActionChecker checker : checkers) {
				checker.checkForAction();
				try {
					Thread.sleep(checkActionInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void addActionChecker(MallobOutputActionChecker checker) {
		this.checkers.add(checker);
	}

}
