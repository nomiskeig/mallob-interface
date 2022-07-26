package edu.kit.fallob.commands;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.dataobjects.JobResult;
import edu.kit.fallob.dataobjects.JobStatus;
import edu.kit.fallob.springConfig.FallobException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

public class JobResultCommand {
	
	private DaoFactory daoFactory;
	private JobDao jobDao;
	private UserActionAuthentificater uaa;
	
	public JobResultCommand() {
		daoFactory = new DaoFactory();
		jobDao = daoFactory.getJobDao();
		uaa = new UserActionAuthentificater(daoFactory);
	}
	
	public JobResult getSingleJobResult(String username, int jobID) throws FallobException {
		if (!uaa.hasResultAccess(username, jobID)) {
			throw new FallobException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
		}
		if (jobDao.getJobStatus(jobID) != JobStatus.DONE) {
			throw new FallobException(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase());
		}
		return jobDao.getJobResult(jobID);
	}
	
	public List<JobResult> getMultipleJobResult(String username, int[] jobIDs) throws FallobException {
		List<JobResult> jobResults = new ArrayList<>();
		for (Integer id : jobIDs) {
			try {
				jobResults.add(getSingleJobResult(username, id));
			} catch (FallobException e) {
				continue;
			}
		}
		if (jobResults.isEmpty()) {
			throw new FallobException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
		}
		return jobResults;
	}
	
	public List<JobResult> getAllJobResult(String username) {
		int[] jobIDs = jobDao.getAllJobIds(username);
		List<JobResult> jobResults = new ArrayList<>();
		try {
			jobResults = getMultipleJobResult(username, jobIDs);
		} catch (FallobException e) {
			
		}
		return jobResults;
	}

}
