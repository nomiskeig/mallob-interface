package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.springConfig.FallobException;

/**
 * 
 *
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class PriorityConverter {
	
	
	private DaoFactory factory;
	
	public PriorityConverter(DaoFactory factory) throws FallobException {
		this.factory = factory;
	}

	
	
	/**
	 * Convert a given job-Priority into a priority, suitable for mallob
	 * @param username of the user which owns the job, which priority was given
	 * @param jobPriority priority of the job, has to be between 0 and 1
	 * @return a double p with the following property : 0 <= p <= 1
	 */
	public double getPriorityForMallob(String username, double jobPriority) throws FallobException {
		if (jobPriority == JobConfiguration.DOUBLE_NOT_SET) {
			return factory.getUserDao().getUserByUsername(username).getPriority();
		}
		return jobPriority * factory.getUserDao().getUserByUsername(username).getPriority();
	}
}
