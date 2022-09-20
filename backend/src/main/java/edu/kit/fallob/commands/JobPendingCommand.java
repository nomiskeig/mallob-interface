package edu.kit.fallob.commands;

import edu.kit.fallob.dataobjects.JobStatus;
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
			throw new FallobException(HttpStatus.FORBIDDEN, "This JobID does not belong to the user");
		}
		if (jobDao.getJobStatus(jobID) != JobStatus.RUNNING) {
			throw new FallobException(HttpStatus.BAD_REQUEST, "This Job is not running anymore");
		}
		//get mallobID
		int mallobID = jobDao.getMallobIdByJobId(jobID);
		JobListener jobListener = new JobListener(mallobID);
		mallobOutput.addOutputLogLineListener(jobListener);
		jobListener.waitForJob();
		mallobOutput.removeOutputLogLineListener(jobListener);
		ResultMetaData rmd = jobDao.getJobInformation(jobID).getResultMetaData();
		if (rmd == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return jobDao.getJobInformation(jobID).getResultMetaData();
	}

}
