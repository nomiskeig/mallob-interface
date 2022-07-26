package edu.kit.fallob.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.dataobjects.JobStatus;
import edu.kit.fallob.mallobio.input.MallobInput;
import edu.kit.fallob.springConfig.FallobException;

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
	
	
	public JobAbortCommands() {
		daoFactory = new DaoFactory();
		jobDao = daoFactory.getJobDao();
		uaa = new UserActionAuthentificater(daoFactory);
		
	}
	
	
	
	public void abortSingleJob(String username, int jobID) throws FallobException {
		if (!uaa.hasAbortAccess(username, jobID)) {
			throw new FallobException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
		}
		
		if (jobDao.getJobStatus(jobID) != JobStatus.RUNNING) {
			throw new FallobException(HttpStatus.CONFLICT, HttpStatus.CONFLICT.getReasonPhrase());
		}
		try {
			mallobInput.abortJob(jobID);
		} catch (IOException e) {
			throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		}
	}
	
	public List<Integer> abortMultipleJobs(String username, int[] jobIDs) throws FallobException {
		List<Integer> canceledJobs = new ArrayList<>();
		for (Integer id : jobIDs) {
			try {
				abortSingleJob(username, id);
				canceledJobs.add(id);
			} catch (FallobException e) {
				continue;
			}
		}
		if (canceledJobs.isEmpty()) {
			throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		}
		return canceledJobs;
	}
	
	public List<Integer> abortAllJobs(String username) throws FallobException {
		JobDao jobDao = daoFactory.getJobDao();
		int[] jobIDs = jobDao.getAllJobIds(username);
		return abortMultipleJobs(username, jobIDs);
	}
	
	public List<Integer> abortAllGlobalJob(String username) throws FallobException {
		if (!uaa.isAdmin(username)) {
			throw new FallobException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
		}
		int[] allGlobalJobIDs = new int[1]; //provisorisch
		return abortMultipleJobs(username, allGlobalJobIDs);
	}

}
