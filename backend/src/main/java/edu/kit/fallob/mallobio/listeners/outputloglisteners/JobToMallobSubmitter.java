package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.kit.fallob.configuration.FallobConfiguration;
import org.springframework.http.HttpStatus;

import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.mallobio.input.MallobInput;
import edu.kit.fallob.mallobio.input.MallobInputImplementation;
import edu.kit.fallob.mallobio.output.MallobOutputWatcherManager;
import edu.kit.fallob.springConfig.FallobException;

/**
 * This class provides the functionality to submit a Job in Mallob.
 * 
 * @author Maik Sept
 * @version 1.0
 *
 */
public class JobToMallobSubmitter implements OutputLogLineListener {
	
	private final static int JOB_IS_SUBMITTING = 0;
	private final static int JOB_IS_VALID = 1;
	private final static int JOB_IS_NOT_VALID = 2;
	private final static String JOB_ID_REGEX = "I Mapping job \"%s.*\" to internal ID #[0-9]+";
	private final static String VALID_JOB_REGEX = "Introducing job #[0-9]+";
	private final static String NOT_VALID_JOB_REGEX = "\\[WARN\\] Rejecting submission %s.* - reason:";
	
	private String username;
	private int jobID;
	private MallobInput mallobInput;
	private Object monitor;
	private int jobStatus = JOB_IS_SUBMITTING;
	private Pattern validJobPattern;
	private Pattern notValidJobPattern;
	private Pattern jobIdPattern;
	private String errorMessage;
	
	public JobToMallobSubmitter(String username) {
		this.username = username;
		this.mallobInput = MallobInputImplementation.getInstance();
		this.monitor = new Object();
		
		String formattedNotValidJobPattern = String.format(NOT_VALID_JOB_REGEX, username);
		String formattedJobIdPattern = String.format(JOB_ID_REGEX, username);
		validJobPattern = Pattern.compile(VALID_JOB_REGEX);
		notValidJobPattern = Pattern.compile(formattedNotValidJobPattern);
		jobIdPattern = Pattern.compile(formattedJobIdPattern);
	}
	
	
	public int submitJobToMallob(JobConfiguration jobConfiguration, JobDescription jobDescription) throws IOException, FallobException {
		
		int clientProcessID = mallobInput.submitJobToMallob(username, jobConfiguration, jobDescription);
		
		synchronized(monitor) {
			while (jobStatus == JOB_IS_SUBMITTING) {
				try {
					monitor.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (jobStatus == JOB_IS_NOT_VALID) {
			throw new FallobException(HttpStatus.BAD_REQUEST, errorMessage);
		}
		
		//job is valid and result can be detected
		MallobOutputWatcherManager watcherManager = MallobOutputWatcherManager.getInstance();
		watcherManager.addNewWatcher(username, jobConfiguration.getName(), clientProcessID);
		
		return jobID;
		
	}


	@Override
	public void processLine(String line) {
		Matcher jobIdMatcher = jobIdPattern.matcher(line);
		if (jobIdMatcher.find()) {
			jobID = Integer.parseInt(line.substring(line.indexOf('#') + 1));
		}
		Matcher validJobMatcher = validJobPattern.matcher(line);
		if (validJobMatcher.find()) {
			jobStatus = JOB_IS_VALID;
			synchronized(monitor) {
				monitor.notify();
			}
		}
		Matcher notValidJobMatcher = notValidJobPattern.matcher(line);
		if (notValidJobMatcher.find()) {
			jobStatus = JOB_IS_NOT_VALID;
			errorMessage = line.substring(line.indexOf("reason") + 8);
			synchronized(monitor) {
				monitor.notify();
			}
		}
		
	}
	
	
	

}
