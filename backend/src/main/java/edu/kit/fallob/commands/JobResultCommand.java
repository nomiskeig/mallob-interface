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
	
	public List<JobResult> getAllJobResult(String username) throws FallobException{
		int[] jobIDs = jobDao.getAllJobIds(username);
		List<JobResult> jobResults = new ArrayList<>();
		try {
			jobResults = getMultipleJobResult(username, jobIDs);
		} catch (FallobException e) {
			
		}
		return jobResults;
	}

}
