package edu.kit.fallob.mallobio.input;

import java.io.IOException;


import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 * 
 * Interface responsible for the input to mallob
 *
 */
public interface MallobInput {
	
	/**
	 * Creates a JSON in the correct format for the Mallob-API, :
	 * 
	 * The creates a .json file, which contains said json and places it in a client-input directory
	 * (or it creates the file in the directory directly, respectively).
	 * 
	 * This directory is generated for each client-process individually by MallobFilePathGenerator
	 * 
	 * 
	 * @see href="https://github.com/domschrei/mallob#introducing-a-job">Link to Mallob API</a>
	 * 
	 * 
	 * @param userName of the user submitting this job
	 * @param jobConfiguration
	 * @param jobDescription
	 * @return ID of the process (mallob) it was committed to
	 * @throws IOException if writing the file was not successful 
	 */
	int submitJobToMallob(String userName, 
			JobConfiguration jobConfiguration, 
			JobDescription jobDescription) throws IOException;

	
	/**
	 * Abort a running job from Mallob, using the mallob API : 
	 * create a .json with the necessary parameters** and place it in a client-input directory.
	 * **necessary parameters : 
	 * --username
	 * --jobName
	 * --interrupt = true
	 * 
	 * @param username of the user who submitted the job
	 * @param jobName of the job which is to be aborted 
	 * @return ID of the process (mallob) that handled the abortion
	 * @throws IOException if writing the file was not successful 
	 */
	int abortJob(String username, String jobName) throws IOException;

}
