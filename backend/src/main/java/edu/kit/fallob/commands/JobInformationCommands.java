package edu.kit.fallob.commands;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.dataobjects.JobInformation;

import java.util.ArrayList;
import java.util.List;


public class JobInformationCommands {
	
	
	private UserActionAuthentificater uaa;
	private DaoFactory daoFactory;
	private JobDao jobDao;
	
	
	
	public JobInformation getSingleJobInformation(String username, int jobID) {
		if (!uaa.hasInformationAccess(username, jobID)) {
			// throw something
		}
		return jobDao.getJobInformation(jobID);
	}
	
	//throw eine exception wenn liste leer?
	public List<JobInformation> getMultipleJobInformation(String username, int[] jobIDs) {
		List<JobInformation> jobInformations = new ArrayList<>();
		for (Integer id : jobIDs) {
			jobInformations.add(getSingleJobInformation(username, id));
		}
		return jobInformations;
	}
	
	public List<JobInformation> getAllJobInformation(String username) {
		int[] jobIDs = jobDao.getAllJobIds(username);
		return getMultipleJobInformation(username, jobIDs);
	}
	
	//trow exception wenn mindestens eine id nicht berechtigt
	public List<JobInformation> getAllGlobalJobInformation(String username) {
		int[] allGlobalJobIDs = new int[1]; //provisorisch
		List<JobInformation> jobInformations = getMultipleJobInformation(username, allGlobalJobIDs);
		return jobInformations;
	}

}
