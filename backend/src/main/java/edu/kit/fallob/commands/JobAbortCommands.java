package edu.kit.fallob.commands;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.dataobjects.JobInformation;
import edu.kit.fallob.dataobjects.JobStatus;
import edu.kit.fallob.mallobio.input.MallobInput;
import edu.kit.fallob.mallobio.input.MallobInputImplementation;
import edu.kit.fallob.springConfig.FallobException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides methods to abort running jobs.
 * 
 * @author Maik Sept
 * @version 1.0
 *
 */
@Service
public class JobAbortCommands {

	
	private DaoFactory daoFactory;
	private MallobInput mallobInput;
	private UserActionAuthentificater uaa;
	private JobDao jobDao;
	
	
	public JobAbortCommands() throws FallobException{
		// TODO Until the data base is fully implemented, we catch the error so the program could be started - should we remove try-catch after that?
		try {
			daoFactory = new DaoFactory();
			jobDao = daoFactory.getJobDao();
			uaa = new UserActionAuthentificater(daoFactory);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		mallobInput = MallobInputImplementation.getInstance();
	}
	
	
	
	public boolean abortSingleJob(String username, int jobID) throws FallobException {
		if (!uaa.hasAbortAccess(username, jobID)) {
			throw new FallobException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
		}
		
		if (jobDao.getJobStatus(jobID) != JobStatus.RUNNING) {
			throw new FallobException(HttpStatus.CONFLICT, HttpStatus.CONFLICT.getReasonPhrase());
		}
		JobInformation jobInfo = jobDao.getJobInformation(jobID);
		try {
			mallobInput.abortJob(username, jobInfo.getJobConfiguration().getName());
		} catch (IOException e) {
			throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		}
        
		return true;
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
		List<Integer> allGlobalJobIDs = jobDao.getAllRunningJobs(); 
		int[] allGlobalJobIDsArray = new int[allGlobalJobIDs.size()];
		for (int i = 0; i < allGlobalJobIDs.size(); i++) {
			allGlobalJobIDsArray[i] = allGlobalJobIDs.get(i);
		}
		return abortMultipleJobs(username, allGlobalJobIDsArray);
	}

}
