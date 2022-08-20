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
		if (!uaa.hasInformationAccess(username, jobID)) {
			throw new FallobException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
		}
		return jobDao.getJobInformation(jobID);
	}
	

	public List<JobInformation> getMultipleJobInformation(String username, int[] jobIDs) throws FallobException {
		List<JobInformation> jobInformations = new ArrayList<>();
		for (Integer id : jobIDs) {
			try {
				jobInformations.add(getSingleJobInformation(username, id));
			} catch (FallobException e) {
				continue;
			}
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
		List<Integer> allGlobalJobIDs = jobDao.getAllRunningJobs(); //provisorisch
		int[] allGlobalJobIDsArray = new int[allGlobalJobIDs.size()];
		for (int i = 0; i < allGlobalJobIDs.size(); i++) {
			allGlobalJobIDsArray[i] = allGlobalJobIDs.get(i);
		}
		return getMultipleJobInformation(username, allGlobalJobIDsArray);
	}

}
