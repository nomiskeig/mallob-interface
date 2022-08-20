package edu.kit.fallob.mallobio.listeners.resultlisteners;

import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.mallobio.outputupdates.ResultAvailableObject;


/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 * 
 * Writes Job-Result into the database 
 *
 */
public class JobResultListener implements ResultObjectListener {
	
	private JobDao dao;
	
	public JobResultListener(JobDao dao) {
		this.dao = dao;
	}

	@Override
	public void processResultObject(ResultAvailableObject rao) {

	}
}
