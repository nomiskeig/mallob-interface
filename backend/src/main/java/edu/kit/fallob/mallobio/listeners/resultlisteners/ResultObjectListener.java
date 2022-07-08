package edu.kit.fallob.mallobio.listeners.resultlisteners;

import edu.kit.fallob.mallobio.outputupdates.ResultAvailableObject;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public interface ResultObjectListener {

	/**
	 * Process a ResultAvailableObject 
	 * 
	 * @param rao
	 */
	void processResultObject(ResultAvailableObject rao);
}
