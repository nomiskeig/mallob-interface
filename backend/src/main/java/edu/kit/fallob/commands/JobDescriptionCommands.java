package edu.kit.fallob.commands;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.dataobjects.JobResult;
import edu.kit.fallob.springConfig.FallobException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

public class JobDescriptionCommands {
	
	private DaoFactory daoFactory;
	private JobDao jobDao;
	private UserActionAuthentificater uaa;
	
	public JobDescriptionCommands() {
		daoFactory = new DaoFactory();
		jobDao = daoFactory.getJobDao();
		uaa = new UserActionAuthentificater(daoFactory);
	}
	
	
	public JobDescription getSingleJobDescription(String username, int jobID) throws FallobException {
		if (!uaa.hasDescriptionAccessViaJobID(username, jobID)) {
			throw new FallobException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
		}
		int descriptionID = jobDao.getJobConfiguration(jobID).getDescriptionID();
		return jobDao.getJobDescription(descriptionID);
	}
	
	public List<JobDescription> getMultipleJobDescription(String username, int[] jobIDs) throws FallobException {
		List<JobDescription> jobDescriptions = new ArrayList<>();
		for (Integer id : jobIDs) {
			try {
				jobDescriptions.add(getSingleJobDescription(username, id));
			} catch (FallobException e) {
				continue;
			}
		}
		if (jobDescriptions.isEmpty()) {
			throw new FallobException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
		}
		return jobDescriptions;
	}
	
	public List<JobDescription> getAllJobDescription(String username) {
		int[] jobIDs = jobDao.getAllJobIds(username);
		List<JobDescription> jobDescriptions = new ArrayList<>();
		try {
			jobDescriptions = getMultipleJobDescription(username, jobIDs);
		} catch (FallobException e) {
			
		}
		return jobDescriptions;
	}

}
