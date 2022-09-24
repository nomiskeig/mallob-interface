package edu.kit.fallob.commands;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.dataobjects.JobInformation;
import edu.kit.fallob.springConfig.FallobException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
public class JobInformationCommands {
	
	
	private UserActionAuthentificater uaa;
	private DaoFactory daoFactory;
	private JobDao jobDao;

	private static final String CONTAINS_INCORRECT_JOBID = "A jobId can not be null";

	private static final String NOT_FOUND_MESSAGE = "No jobs were found that match the given ids";

	private static final String FORBIDDEN_MESSAGE = "The user has no access to the given jobs";

	private static final String INTERNAL_SERVER_ERROR_MESSAGE = "The jobInformation could not be printed, as " +
			"either the user has no access to it or the job could not be found";

	
	public JobInformationCommands() throws FallobException{
		// TODO Until the data base is fully implemented, we catch the error so the program could be started - should we remove try-catch after that?
		try {
			daoFactory = new DaoFactory();
			jobDao = daoFactory.getJobDao();
			uaa = new UserActionAuthentificater(daoFactory);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	
	public JobInformation getSingleJobInformation(String username, int jobID) throws FallobException {
		if (jobID <= 0) {
			throw new FallobException(HttpStatus.BAD_REQUEST, CONTAINS_INCORRECT_JOBID);
		}
		if (!uaa.hasInformationAccess(username, jobID)) {
			throw new FallobException(HttpStatus.FORBIDDEN, FORBIDDEN_MESSAGE);
		}
		return jobDao.getJobInformation(jobID);
	}
	

	public List<JobInformation> getMultipleJobInformation(String username, int[] jobIDs) throws FallobException {
		List<JobInformation> jobInformations = new ArrayList<>();
		int statusForbiddenCounter = 0;
		int statusNotFoundCounter = 0;
		for (Integer id : jobIDs) {
			try {
				jobInformations.add(getSingleJobInformation(username, id));
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
            return jobInformations;
        }

		if (statusForbiddenCounter == jobIDs.length) {
			throw new FallobException(HttpStatus.FORBIDDEN, FORBIDDEN_MESSAGE);
		}
		else if (statusNotFoundCounter == jobIDs.length) {
			throw new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE);
		}

		if (jobInformations.isEmpty()) {
			throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
		}
		return jobInformations;
	}
	
	public List<JobInformation> getAllJobInformation(String username) throws FallobException {
		int[] jobIDs = jobDao.getAllJobIds(username);
		return getMultipleJobInformation(username, jobIDs);
	}
	
	
	public List<JobInformation> getAllGlobalJobInformation(String username) throws FallobException {
		if (!uaa.isAdmin(username)) {
			throw new FallobException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
		}
		List<Integer> allGlobalJobIDs = jobDao.getAllJobIds();
		int[] allGlobalJobIDsArray = new int[allGlobalJobIDs.size()];
		for (int i = 0; i < allGlobalJobIDs.size(); i++) {
			allGlobalJobIDsArray[i] = allGlobalJobIDs.get(i);
		}
		return getMultipleJobInformation(username, allGlobalJobIDsArray);
	}

}
