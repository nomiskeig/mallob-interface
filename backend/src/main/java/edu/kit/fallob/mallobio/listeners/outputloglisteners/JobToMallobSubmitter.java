package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import java.io.IOException;

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
	
	
	private String username;
	private int jobID;
	private MallobInput mallobInput;
	private Object monitor;
	private int jobStatus = JOB_IS_SUBMITTING;
	
	
	public JobToMallobSubmitter(String username) {
		this.username = username;
		this.mallobInput = MallobInputImplementation.getInstance();
		this.monitor = new Object();
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
			throw new FallobException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase());
		}
		
		//job is valid and result can be detected
		MallobOutputWatcherManager watcherManager = MallobOutputWatcherManager.getInstance();
		watcherManager.addNewWatcher(username, jobConfiguration.getName(), clientProcessID);
		
		return jobID;
		
	}


	@Override
	public void processLine(String line) {
		//control the logline and set job status and jobID
		
		synchronized(monitor) {
			monitor.notify();
		}
	}
	
	
	

}
