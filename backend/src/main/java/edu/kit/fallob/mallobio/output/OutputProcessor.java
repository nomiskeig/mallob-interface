package edu.kit.fallob.mallobio.output;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 * 
 * This interface makes it possible to create listeners for a SPECIFIC log-process, instead of all processes.
 */
public interface OutputProcessor {
	
	/**
	 * Process a line, given 
	 * 
	 * @param logLine
	 */
	void processLogLine(String logLine);
	

}
