package edu.kit.fallob.commands;

import edu.kit.fallob.dataobjects.ResultMetaData;
import org.springframework.http.HttpStatus;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.JobListener;
import edu.kit.fallob.mallobio.output.distributors.MallobOutput;
import edu.kit.fallob.springConfig.FallobException;
import org.springframework.stereotype.Service;

@Service
public class JobPendingCommand {
	
	private MallobOutput mallobOutput;
	private DaoFactory daoFactory;
	private UserActionAuthentificater uaa;
	private JobDao jobDao;
	
	
	
	public JobPendingCommand() throws FallobException{
		// TODO Until the data base is fully implemented, we catch the error so the program could be started - should we remove try-catch after that?
		try {
			daoFactory = new DaoFactory();
			uaa = new UserActionAuthentificater(daoFactory);
			mallobOutput = MallobOutput.getInstance();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		jobDao = daoFactory.getJobDao();
	}
	
	public ResultMetaData waitForJob(String username, int jobID) throws FallobException {
		if (!uaa.isOwnerOfJob(username, jobID)) {
			throw new FallobException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
		}
		//get mallobID
		int mallobID = jobDao.getMallobIdByJobId(jobID);
		JobListener jobListener = new JobListener(mallobID);
		mallobOutput.addOutputLogLineListener(jobListener);
		jobListener.waitForJob();
		mallobOutput.removeOutputLogLineListener(jobListener);

		//TODO Temporary fix from kalo (need an ResultMetaData-Object of the job the user is waiting for)
		
		return jobDao.getJobInformation(jobID).getResultMetaData();
	}

}
