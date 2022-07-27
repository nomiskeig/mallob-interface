package edu.kit.fallob.mallobio.output;

public interface MallobOutputActionChecker {

	/**
	 * Checks for action done by mallob
	 */
	void checkForAction();
	
	
	/**
	 * @return true if the output processor is done 
	 */
	boolean isDone();
}
