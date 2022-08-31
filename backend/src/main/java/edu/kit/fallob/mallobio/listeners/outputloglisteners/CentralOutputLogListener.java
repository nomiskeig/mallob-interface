package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import edu.kit.fallob.mallobio.listeners.resultlisteners.ResultObjectListener;
import edu.kit.fallob.mallobio.outputupdates.ResultAvailableObject;

/**
 * 
 * @author Simon Wilhelm Schübel
 * 
 * 
 * Prints all Logs and found results to the console 
 *
 */
public class CentralOutputLogListener implements OutputLogLineListener, ResultObjectListener {

	@Override
	public void processLine(String line) {
		System.out.println(line);
	}

	@Override
	public void processResultObject(ResultAvailableObject rao) {
		System.out.println("FOUND RESULT AT PATH : '" + rao.getFilePathToResult().toString() + "'");
	}

	
}
