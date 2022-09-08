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
				e.printStackTrace();
				continue;
			}
		}
		if (jobDescriptions.isEmpty()) {
			throw new FallobException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
		}
		return jobDescriptions;
	}
	
	public List<JobDescription> getAllJobDescription(String username) throws FallobException {
		int[] jobIDs = jobDao.getAllJobIds(username);
		List<JobDescription> jobDescriptions;
		jobDescriptions = getMultipleJobDescription(username, jobIDs);
		return jobDescriptions;
	}

}
