package edu.kit.fallob.commands;

import org.springframework.http.HttpStatus;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.JobListener;
import edu.kit.fallob.mallobio.output.distributors.MallobOutput;
import edu.kit.fallob.springConfig.FallobException;

public class JobPendingCommand {
	
	private MallobOutput mallobOutput;
	private DaoFactory daoFactory;
	private UserActionAuthentificater uaa;
	
	
	
	public JobPendingCommand() {
		daoFactory = new DaoFactory();
		uaa = new UserActionAuthentificater(daoFactory);
	}
	
	public void waitForJob(String username, int jobID) throws FallobException {
		if (!uaa.isOwnerOfJob(username, jobID)) {
			throw new FallobException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
		}
		//get mallobID
		int mallobID = 0;
		JobListener jobListener = new JobListener(mallobID);
		mallobOutput.addOutputLogLineListener(jobListener);
		jobListener.waitForJob();
		mallobOutput.removeOutputLogLineListener(jobListener);
	}

}
