package edu.kit.fallob.commands;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.springConfig.FallobException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobDescriptionCommands {
	
	private DaoFactory daoFactory;
	private JobDao jobDao;
	private UserActionAuthentificater uaa;

	private static final String CONTAINS_INCORRECT_JOBID = "A jobId can not be null";

	private static final String NOT_FOUND_MESSAGE = "No jobs were found that match the given ids";

	private static final String FORBIDDEN_MESSAGE = "The user has no access to the given jobs";

	private static final String INTERNAL_SERVER_ERROR_MESSAGE = "The description could not be printed, as " +
			"either the user has no access to it or the job could not be found";
	
	public JobDescriptionCommands() throws FallobException{
		// TODO Until the data base is fully implemented, we catch the error so the program could be started - should we remove try-catch after that?
		try {
			daoFactory = new DaoFactory();
			jobDao = daoFactory.getJobDao();
			uaa = new UserActionAuthentificater(daoFactory);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
	
	public JobDescription getSingleJobDescription(String username, int jobID) throws FallobException {
		if (jobID <= 0) {
			throw new FallobException(HttpStatus.BAD_REQUEST, CONTAINS_INCORRECT_JOBID);
		}
		if (!uaa.hasDescriptionAccessViaJobID(username, jobID)) {
			throw new FallobException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
		}
		int descriptionID = jobDao.getJobConfiguration(jobID).getDescriptionID();
		return jobDao.getJobDescription(descriptionID);
	}
	
	public List<JobDescription> getMultipleJobDescription(String username, int[] jobIDs) throws FallobException {
		List<JobDescription> jobDescriptions = new ArrayList<>();
		int statusForbiddenCounter = 0;
		int statusNotFoundCounter = 0;
		for (Integer id : jobIDs) {
			try {
				jobDescriptions.add(getSingleJobDescription(username, id));
			} catch (FallobException e) {
				if (e.getStatus().equals(HttpStatus.NOT_FOUND)) {
					statusNotFoundCounter++;
				}
				else if (e.getStatus().equals(HttpStatus.FORBIDDEN)) {
					statusForbiddenCounter++;
				}
			}
		}

		if (statusForbiddenCounter == jobIDs.length) {
			throw new FallobException(HttpStatus.FORBIDDEN, FORBIDDEN_MESSAGE);
		}
		else if (statusNotFoundCounter == jobIDs.length) {
			throw new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE);
		}

		if (jobDescriptions.isEmpty()) {
			throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
		}
		return jobDescriptions;
	}
	
	public List<JobDescription> getAllJobDescription(String username) throws FallobException {
		int[] jobIDs = jobDao.getAllJobIds(username);
		List<JobDescription> jobDescriptions = new ArrayList<>();
		try {
			jobDescriptions = getMultipleJobDescription(username, jobIDs);
		} catch (FallobException ignored) {
			
		}
		return jobDescriptions;
	}

}
