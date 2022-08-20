package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.mallobio.outputupdates.StatusUpdate;
import edu.kit.fallob.springConfig.FallobException;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class JobStatusListener implements OutputLogLineListener {

	
	private JobDao jobDao;
	
	public JobStatusListener() {
		DaoFactory daoFactory = null;
		try {
			daoFactory = new DaoFactory();
		} catch (FallobException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jobDao = daoFactory.getJobDao();
	}

	@Override
	public void processLine(String line) {
		if (StatusUpdate.isJobStatus(line)) {
			StatusUpdate statusUpdate = new StatusUpdate(line);
			try {

				jobDao.updateJobStatus(jobDao.getJobIdByMallobId(statusUpdate.getJobID()), statusUpdate.getJobStatus());
			} catch (FallobException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
