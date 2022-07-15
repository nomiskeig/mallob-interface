package edu.kit.fallob.commands;


import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.JobToMallobSubmitter;
import edu.kit.fallob.mallobio.output.distributors.MallobOutput;

/**
 * This class provides methods which submit a new Job, restart a canceled Job or save a new Jobdescription.
 * 
 * @author Maik Sept
 * @version 1.0
 *
 */
public class JobSubmitCommands {
	
	
	private DaoFactory daoFactory;
	private MallobOutput mallobOutput;
	private UserActionAuthentificater uaa;
	
	
	
	private int submitJob(String username, JobDescription jobDescription, JobConfiguration jobConfiguration) throws InvalidJobException { 
		JobToMallobSubmitter submitter = new JobToMallobSubmitter(username);
		mallobOutput.addOutputLogLineListener(submitter);
		int mallobID = submitter.submitJobToMallob(jobConfiguration, jobDescription);
		mallobOutput.addOutputLogLineListener(submitter);
		return mallobID;
	}
	
	
	public int submitJobWithDescription(String username, JobDescription jobDescription, JobConfiguration jobConfiguration) throws InvalidJobException {
		int mallobID = submitJob(username, jobDescription, jobConfiguration);
		JobDao jobDao = daoFactory.getJobDao();
		int descriptionID = jobDao.saveJobDescription(jobDescription, username, jobDescription.getSubmitType());
		jobConfiguration.setDescriptionID(descriptionID);
		return jobDao.saveJobConfiguration(jobConfiguration, username, mallobID);
		
	}
	
	public int submitJobWithDescriptionID(String username, int jobdescriptionID, JobConfiguration jobConfiguration) throws InvalidJobException {
		if (!uaa.hasDescriptionAccessViaDescriptionID(username, jobdescriptionID)) {
			//throw exception if job
		}
		JobDao jobDao = daoFactory.getJobDao();
		JobDescription jobDescription = jobDao.getJobDescription(jobdescriptionID);
		int mallobID = submitJob(username, jobDescription, jobConfiguration);
		jobConfiguration.setDescriptionID(jobdescriptionID);
		return jobDao.saveJobConfiguration(jobConfiguration, username, mallobID);
	}
	
	public int restartCanceledJob(String username, int jobID) throws InvalidJobException {
		if (!uaa.isOwnerOfJob(username, jobID)) {
			//throw Exception
		}
		JobDao jobDao = daoFactory.getJobDao();
		JobConfiguration jobConfiguration = jobDao.getJobConfiguration(jobID);
		JobDescription jobDescription = jobDao.getJobDescription(jobConfiguration.getDescriptionID());
		int mallobID = submitJob(username, jobDescription, jobConfiguration);
		return jobDao.saveJobConfiguration(jobConfiguration, username, mallobID);
	}
	
	public int saveJobDescription(String username, JobDescription jobDescription) {
		JobDao jobDao = daoFactory.getJobDao();
		return jobDao.saveJobDescription(jobDescription, username, jobDescription.getSubmitType());
	}

}
