package edu.kit.fallob.commands;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.database.UserDao;
import edu.kit.fallob.dataobjects.JobInformation;
import edu.kit.fallob.dataobjects.JobStatus;
import edu.kit.fallob.mallobio.input.MallobInput;
import edu.kit.fallob.mallobio.input.MallobInputImplementation;
import edu.kit.fallob.springConfig.FallobException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides methods to abort running jobs.
 * 
 * @author Maik Sept
 * @version 1.0
 *
 */
@Service
public class JobAbortCommands {

	
	private DaoFactory daoFactory;
	private MallobInput mallobInput;
	private UserActionAuthentificater uaa;
	private JobDao jobDao;

	private UserDao userDao;

	private static final String CONTAINS_INCORRECT_JOBID = "A jobId can not be null.";

	private static final String NOT_FOUND_MESSAGE = "No jobs were found that match the given ids.";

	private static final String FORBIDDEN_MESSAGE = "The user has no access to the given jobs.";

	private static final String FORBIDDEN_MESSAGE_SINGLE = "The user has no access to the given job.";

	private static final String CONFLICT_MESSAGE = "No jobs could be cancelled, as they are not active.";

	private static final String CONFLICT_MESSAGE_SINGLE = "Job could not be cancelled, as it is not active.";

	private static final String INTERNAL_SERVER_ERROR_MESSAGE = "The jobs could not be cancelled, where " +
			"either the user has no access to it, the job could not be found or it is not running.";
	
	public JobAbortCommands() throws FallobException{
		try {
			daoFactory = new DaoFactory();
			jobDao = daoFactory.getJobDao();
			userDao = daoFactory.getUserDao();
			uaa = new UserActionAuthentificater(daoFactory);
			mallobInput = MallobInputImplementation.getInstance();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		mallobInput = MallobInputImplementation.getInstance();
	}
	
	
	
	public boolean abortSingleJob(String username, int jobID, boolean incremental) throws FallobException {
		if (jobID <= 0) {
			throw new FallobException(HttpStatus.BAD_REQUEST, CONTAINS_INCORRECT_JOBID);
		}

		if (!uaa.hasAbortAccess(username, jobID)) {
			throw new FallobException(HttpStatus.FORBIDDEN, FORBIDDEN_MESSAGE_SINGLE);
		}
		
		if (jobDao.getJobStatus(jobID) != JobStatus.RUNNING) {
			throw new FallobException(HttpStatus.CONFLICT, CONFLICT_MESSAGE_SINGLE);
		}

		if (uaa.isAdmin(username)) {
			username = userDao.getUsernameByJobId(jobID);
		}
		JobInformation jobInfo = jobDao.getJobInformation(jobID);
		try {
			mallobInput.abortJob(username, jobInfo.getJobConfiguration().getName(), incremental);
		} catch (IOException e) {
			throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
		}
        
		return true;
	}
	
	public List<Integer> abortMultipleJobs(String username, int[] jobIDs) throws FallobException {
		List<Integer> cancelledJobs = new ArrayList<>();
		int statusConflictCounter = 0;
		int statusForbiddenCounter = 0;
		int statusNotFoundCounter = 0;
		for (Integer id : jobIDs) {
			try {
				//check if the status of the job is running
				if (this.jobDao.getJobStatus(id).equals(JobStatus.RUNNING)) {
					abortSingleJob(username, id, false);
					cancelledJobs.add(id);
				}
			} catch (FallobException e) {
				e.printStackTrace();
				if (e.getStatus().equals(HttpStatus.CONFLICT)) {
					statusConflictCounter++;
				}
				else if (e.getStatus().equals(HttpStatus.NOT_FOUND)) {
					statusNotFoundCounter++;
				}
				else if (e.getStatus().equals(HttpStatus.FORBIDDEN)) {
					statusForbiddenCounter++;
				}
			}
		}
        if (jobIDs.length == 0) {
            return cancelledJobs;
        }
		if (statusConflictCounter == jobIDs.length) {
			throw new FallobException(HttpStatus.CONFLICT, CONFLICT_MESSAGE);
		}
		else if (statusForbiddenCounter == jobIDs.length) {
			throw new FallobException(HttpStatus.FORBIDDEN, FORBIDDEN_MESSAGE);
		}
		else if (statusNotFoundCounter == jobIDs.length) {
			throw new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE);
		}

		if (cancelledJobs.isEmpty()) {
			throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
		}
		return cancelledJobs;
	}
	
	public List<Integer> abortAllJobs(String username) throws FallobException {
		JobDao jobDao = daoFactory.getJobDao();
		int[] jobIDs = jobDao.getAllJobsWithStatus(username, JobStatus.RUNNING);

		return this.abortMultipleJobs(username, jobIDs);
	}
	
	public List<Integer> abortAllGlobalJob(String username) throws FallobException {
		if (!uaa.isAdmin(username)) {
			throw new FallobException(HttpStatus.FORBIDDEN, FORBIDDEN_MESSAGE);
		}
		List<Integer> allGlobalJobIDs = jobDao.getAllRunningJobs(); 
		int[] allGlobalJobIDsArray = new int[allGlobalJobIDs.size()];
		for (int i = 0; i < allGlobalJobIDs.size(); i++) {
			allGlobalJobIDsArray[i] = allGlobalJobIDs.get(i);
		}
		return abortMultipleJobs(username, allGlobalJobIDsArray);
	}

}
