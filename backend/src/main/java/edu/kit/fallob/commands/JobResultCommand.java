package edu.kit.fallob.commands;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.dataobjects.JobResult;
import edu.kit.fallob.dataobjects.JobStatus;
import edu.kit.fallob.springConfig.FallobException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class JobResultCommand {
	
	private DaoFactory daoFactory;
	private JobDao jobDao;
	private UserActionAuthentificater uaa;

	private static final String CONTAINS_INCORRECT_JOBID = "A jobId can not be null";

	private static final String NOT_FOUND_MESSAGE = "No jobs were found that match the given ids";

	private static final String FORBIDDEN_MESSAGE = "The user has no access to the given jobs";

	private static final String INTERNAL_SERVER_ERROR_MESSAGE = "The result could not be printed, as " +
			"either the user has no access to it or the job could not be found";

	public JobResultCommand()throws FallobException {
		// TODO Until the data base is fully implemented, we catch the error so the program could be started - should we remove try-catch after that?
		try {
			daoFactory = new DaoFactory();
			jobDao = daoFactory.getJobDao();
			uaa = new UserActionAuthentificater(daoFactory);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public JobResult getSingleJobResult(String username, int jobID) throws FallobException {
		if (jobID <= 0) {
			throw new FallobException(HttpStatus.BAD_REQUEST, CONTAINS_INCORRECT_JOBID);
		}
		if (!uaa.hasResultAccess(username, jobID)) {
			throw new FallobException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
		}
		if (jobDao.getJobStatus(jobID) == JobStatus.RUNNING) {
			throw new FallobException(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase());
		}
		return jobDao.getJobResult(jobID);
	}
	
	public List<JobResult> getMultipleJobResult(String username, int[] jobIDs) throws FallobException {
		List<JobResult> jobResults = new ArrayList<>();
		int statusForbiddenCounter = 0;
		int statusNotFoundCounter = 0;
		for (Integer id : jobIDs) {
			try {
				jobResults.add(getSingleJobResult(username, id));
			} catch (FallobException e) {
				if (e.getStatus().equals(HttpStatus.NOT_FOUND)) {
					statusNotFoundCounter++;
				}
				else if (e.getStatus().equals(HttpStatus.FORBIDDEN)) {
					statusForbiddenCounter++;
				}
				e.printStackTrace();
			}
		}
        if (jobIDs.length == 0) {
            return jobResults;
        }

		if (statusForbiddenCounter == jobIDs.length) {
			throw new FallobException(HttpStatus.FORBIDDEN, FORBIDDEN_MESSAGE);
		}
		else if (statusNotFoundCounter == jobIDs.length) {
			throw new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE);
		}

		if (jobResults.isEmpty()) {
			throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
		}
		return jobResults;
	}
	
	public List<JobResult> getAllJobResult(String username) throws FallobException{
		int[] jobIDs = jobDao.getAllJobIds(username);
		List<JobResult> jobResults;
		jobResults = getMultipleJobResult(username, jobIDs);
		return jobResults;
	}

}
