package edu.kit.fallob.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.dataobjects.JobStatus;
import edu.kit.fallob.mallobio.input.MallobInput;

/**
 * This class provides methods to abort running jobs.
 * 
 * @author Maik Sept
 * @version 1.0
 *
 */
public class JobAbortCommands {
	
	private DaoFactory daoFactory;
	private MallobInput mallobInput;
	private UserActionAuthentificater uaa;
	private JobDao jobDao;
	
	
	
	public boolean abortSingleJob(String username, int jobID) {
		if (!uaa.hasAbortAccess(username, jobID)) {
			return false;
		}
		if (jobDao.getJobStatus(jobID) != JobStatus.RUNNING) {
			return false;
		}
		return mallobInput.abortJob(jobID);
	}
	
	public List<Integer> abortMultipleJobs(String username, int[] jobIDs) {
		List<Integer> canceledJobs = new ArrayList<>();
		for (Integer id : jobIDs) {
			if (abortSingleJob(username, id)) {
				canceledJobs.add(id);
			}
		}
		return canceledJobs;
	}
	
	public List<Integer> abortAllJobs(String username) {
		JobDao jobDao = daoFactory.getJobDao();
		int[] jobIDs = jobDao.getAllJobIds(username);
		return abortMultipleJobs(username, jobIDs);
	}
	
	public List<Integer> abortAllGlobalJob(String username) {
		int[] allGlobalJobIDs = new int[1]; //provisorisch
		return abortMultipleJobs(username, allGlobalJobIDs);
	}

}
