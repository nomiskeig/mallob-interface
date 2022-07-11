package edu.kit.fallob.mallobio.output;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public interface OutputProcessor {


	/**
	 * Process a line, given 
	 * 
	 * @param logLine
	 */
	void processLogLine(String logLine);
}
