package edu.kit.fallob.commands;


import java.io.IOException;

import org.springframework.http.HttpStatus;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.database.UserDao;
import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.JobToMallobSubmitter;
import edu.kit.fallob.mallobio.output.distributors.MallobOutput;
import edu.kit.fallob.springConfig.FallobException;

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
	
	
	public JobSubmitCommands() throws FallobException{
		daoFactory = new DaoFactory();
		
		uaa = new UserActionAuthentificater(daoFactory);
	}
	
	
	private int submitJob(String username, JobDescription jobDescription, JobConfiguration jobConfiguration) throws FallobException { 
		JobToMallobSubmitter submitter = new JobToMallobSubmitter(username);
		mallobOutput.addOutputLogLineListener(submitter);
		int mallobID;
		try {
			mallobID = submitter.submitJobToMallob(jobConfiguration, jobDescription);
		} catch (FallobException e) {
			mallobOutput.removeOutputLogLineListener(submitter);
			throw e;
		} catch (IOException eio) {
			mallobOutput.removeOutputLogLineListener(submitter);
			throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		}
		mallobOutput.removeOutputLogLineListener(submitter);
		return mallobID;
	}
	
	
	public int submitJobWithDescription(String username, JobDescription jobDescription, JobConfiguration jobConfiguration) throws FallobException {
		formatConfiguration(jobConfiguration);
		int mallobID = submitJob(username, jobDescription, jobConfiguration);
		JobDao jobDao = daoFactory.getJobDao();
		int descriptionID = jobDao.saveJobDescription(jobDescription, username, jobDescription.getSubmitType());
		jobConfiguration.setDescriptionID(descriptionID);
		return jobDao.saveJobConfiguration(jobConfiguration, username, mallobID);
		
	}
	
	public int submitJobWithDescriptionID(String username, int jobdescriptionID, JobConfiguration jobConfiguration) throws FallobException {
		if (!uaa.hasDescriptionAccessViaDescriptionID(username, jobdescriptionID)) {
			throw new FallobException(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase());
		}
		JobDao jobDao = daoFactory.getJobDao();
		JobDescription jobDescription = jobDao.getJobDescription(jobdescriptionID);
		formatConfiguration(jobConfiguration);
		int mallobID = submitJob(username, jobDescription, jobConfiguration);
		jobConfiguration.setDescriptionID(jobdescriptionID);
		return jobDao.saveJobConfiguration(jobConfiguration, username, mallobID);
	}
	
	public int restartCanceledJob(String username, int jobID) throws FallobException {
		if (!uaa.isOwnerOfJob(username, jobID)) {
			throw new FallobException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
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
	
	private void formatConfiguration(JobConfiguration jobConfiguration) {
		JobDao jobDao = daoFactory.getJobDao();
		//int[] dependencies = jobConfiguration.getDependencies();
        int[] dependencies = {1,2};
		int precursor = jobConfiguration.getPrecursor();
		if (dependencies != null) {
			String[] jobNames = new String[dependencies.length];
			for (int i = 0; i < dependencies.length; i++) {
				jobNames[i] = jobDao.getJobConfiguration(dependencies[i]).getName();
			}
			jobConfiguration.setDependenciesStrings(jobNames);
		}
		//if (precursor != JobConfiguration.NOT_SET) {
	//		jobConfiguration.setPrecursorString(jobDao.getJobConfiguration(precursor).getName());
	//	}
		
	}

}
