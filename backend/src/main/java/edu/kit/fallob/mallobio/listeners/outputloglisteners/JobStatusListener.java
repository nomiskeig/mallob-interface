package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.mallobio.outputupdates.StatusUpdate;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class JobStatusListener implements OutputLogLineListener {

	
	private JobDao jobDao;

	@Override
	public void processLine(String line) {
		if (StatusUpdate.isJobStatus(line)) {
			//???
		}
	}

}
