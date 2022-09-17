package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.User;
import edu.kit.fallob.springConfig.FallobException;
import org.springframework.http.HttpStatus;

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
		User user = factory.getUserDao().getUserByUsername(username);

		if (user == null) {
			throw new FallobException(HttpStatus.NOT_FOUND, "Error, the user could not be found in the database");
		}
		if (jobPriority == JobConfiguration.DOUBLE_NOT_SET) {
			return user.getPriority();
		}
		return jobPriority * user.getPriority();
	}
}
