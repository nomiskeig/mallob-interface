package edu.kit.fallob.commands;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.JobListener;
import edu.kit.fallob.mallobio.output.distributors.MallobOutput;

public class JobPendingCommand {
	
	private MallobOutput mallobOutput;
	private DaoFactory daoFactory;
	private JobDao jobDao;
	
	
	public JobPendingCommand() {
		daoFactory = new DaoFactory();
		jobDao = daoFactory.getJobDao();
	}
	
	public void waitForJob(String username, int jobID) {
		//check userAccess and
		//get mallobID
		int mallobID = 0;
		JobListener jobListener = new JobListener(mallobID);
		mallobOutput.addOutputLogLineListener(jobListener);
		jobListener.waitForJob();
		mallobOutput.removeOutputLogLineListener(jobListener);
	}

}
