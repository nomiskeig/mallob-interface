package edu.kit.fallob.commands;


import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.database.UserDao;
import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.dataobjects.JobStatus;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.JobToMallobSubmitter;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.MallobTimeListener;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.PriorityConverter;
import edu.kit.fallob.mallobio.output.distributors.MallobOutput;
import edu.kit.fallob.springConfig.FallobException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * This class provides methods which submit a new Job, restart a canceled Job or save a new Jobdescription.
 * 
 * @author Maik Sept
 * @version 1.0
 *
 */
@Service
public class JobSubmitCommands {


	private DaoFactory daoFactory;

	private JobDao jobDao;
	private MallobOutput mallobOutput;
	private UserActionAuthentificater uaa;

	private static final String RESTART_SUFFIX = "_restart";
	
	
	public JobSubmitCommands() throws FallobException{
		// TODO Until the data base is fully implemented, we catch the error so the program could be started - should we remove try-catch after that?
		try {
			daoFactory = new DaoFactory();
			this.jobDao = daoFactory.getJobDao();
			uaa = new UserActionAuthentificater(daoFactory);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		mallobOutput = MallobOutput.getInstance();

	}
	
	
	private int submitJobToMallob(String username, JobDescription jobDescription, JobConfiguration jobConfiguration) throws FallobException {
        if (username == null) {
            throw new FallobException(HttpStatus.BAD_REQUEST, "Username can not be null");
        }
		jobConfigIsOk(jobConfiguration);
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
			throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not access the file: "+  eio.getMessage());
		}
		mallobOutput.removeOutputLogLineListener(submitter);
		return mallobID;
	}
	
	/**
	 * Set default parameter of jobConfiguration if not set, or check if parameteres are correct, if set
	 * @param jobConfiguration
	 * @return
	 */
	private void jobConfigIsOk(JobConfiguration jobConfiguration) throws FallobException{
		if (jobConfiguration.getPriority() == JobConfiguration.DOUBLE_NOT_SET) {
			jobConfiguration.setPriority(FallobConfiguration.getInstance().getDefaultJobPriority());
		}
		if (jobConfiguration.getWallClockLimit() == JobConfiguration.OBJECT_NOT_SET) {
			jobConfiguration.setWallClockLimit(FallobConfiguration.getInstance().getDefaultWallClockLimit());
		}
		if (jobConfiguration.getContentMode() == JobConfiguration.OBJECT_NOT_SET) {
			jobConfiguration.setContentMode(FallobConfiguration.getInstance().getDefaultContentMode());
		}
		if (jobConfiguration.getName() == null) {
            throw new FallobException(HttpStatus.BAD_REQUEST, "Name is required but not provided.");

        }
        if (jobConfiguration.getApplication() == null) {
            throw new FallobException(HttpStatus.BAD_REQUEST, "Application is required but not provided");
		}
		
		//perform checks 
        double minPrio = FallobConfiguration.getInstance().getMinJobPriority();
        double maxPrio = FallobConfiguration.getInstance().getMaxJobPriority();
		if (jobConfiguration.getPriority() < minPrio
				|| jobConfiguration.getPriority() > maxPrio) {
            throw new FallobException(HttpStatus.BAD_REQUEST, "The priority has to be between " + minPrio + " and " + maxPrio);
            
		}
    }

	
	
	
	public int submitJobWithDescriptionInclusive(String username, JobDescription jobDescription, JobConfiguration jobConfiguration) throws FallobException {

//		if (!userDao.getUserByUsername(username).isVerified()) {
//			throw new FallobException(HttpStatus.FORBIDDEN, USER_NOT_VERIFIED);
		formatConfiguration(username, jobConfiguration);
		int descriptionID = jobDao.saveJobDescription(jobDescription, username);
		jobConfiguration.setDescriptionID(descriptionID);
        JobDescription newDescrption = jobDao.getJobDescription(descriptionID);
		int mallobID = submitJobToMallob(username, newDescrption, jobConfiguration);
		return jobDao.saveJobConfiguration(jobConfiguration, username, mallobID);
		
	}

	
	public int submitJobWithDescriptionID(String username, int jobdescriptionID, JobConfiguration jobConfiguration) throws FallobException {
//		if (!userDao.getUserByUsername(username).isVerified()) {
//			throw new FallobException(HttpStatus.FORBIDDEN, USER_NOT_VERIFIED);
//		}
		if (!uaa.hasDescriptionAccessViaDescriptionID(username, jobdescriptionID)) {
			throw new FallobException(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase());
		}
		JobDescription jobDescription = jobDao.getJobDescription(jobdescriptionID);
		formatConfiguration(username, jobConfiguration);
		int mallobID = submitJobToMallob(username, jobDescription, jobConfiguration);
		jobConfiguration.setDescriptionID(jobdescriptionID);
		return jobDao.saveJobConfiguration(jobConfiguration, username, mallobID);
	}
	
	
	public int restartCanceledJob(String username, int jobID) throws FallobException {
		if (!uaa.isOwnerOfJob(username, jobID)) {
			throw new FallobException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
		}
		if (jobDao.getJobStatus(jobID) == JobStatus.RUNNING) {
			throw new FallobException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase());
		}
		JobConfiguration jobConfiguration = jobDao.getJobConfiguration(jobID);
		JobDescription jobDescription = jobDao.getJobDescription(jobConfiguration.getDescriptionID());
		jobConfiguration.setName(formatRestartJobName(jobConfiguration.getName()));
		int mallobID = submitJobToMallob(username, jobDescription, jobConfiguration);
		return jobDao.saveJobConfiguration(jobConfiguration, username, mallobID);
	}
	
	public int saveJobDescription(String username, JobDescription jobDescription) throws FallobException {
//		if (!userDao.getUserByUsername(username).isVerified()) {
//			throw new FallobException(HttpStatus.FORBIDDEN, USER_NOT_VERIFIED);
//		}
		return jobDao.saveJobDescription(jobDescription, username);
	}
	
	private void formatConfiguration(String username, JobConfiguration jobConfiguration) throws FallobException {
	    Integer[] dependencies = jobConfiguration.getDependencies();
		int precursor = jobConfiguration.getPrecursor();
		if (dependencies != null) {
			String[] jobNames = new String[dependencies.length];
			for (int i = 0; i < dependencies.length; i++) {
				jobNames[i] = jobDao.getJobConfiguration(dependencies[i]).getName();
			}
			jobConfiguration.setDependenciesStrings(jobNames);
		}
		if (precursor != JobConfiguration.INT_NOT_SET) {
			jobConfiguration.setPrecursorString(jobDao.getJobConfiguration(precursor).getName());
		}
		if (jobConfiguration.getPriority() != JobConfiguration.DOUBLE_NOT_SET) {
			PriorityConverter prioConverter = new PriorityConverter(daoFactory);
			jobConfiguration.setPriority(prioConverter.getPriorityForMallob(username, jobConfiguration.getPriority()));
		}
		
	}
	
	private String formatRestartJobName(String name) {
		if (name.endsWith(RESTART_SUFFIX)) {
			return name;
		}
		return name + RESTART_SUFFIX;
	}

}
