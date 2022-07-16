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
	 * 	@see href="https://github.com/domschrei/mallob#introducing-a-job">Link to Mallob API</a>
	 * 
	 * The creates a .json file, which contains said json and places it in a client-input directory
	 * (or it creates the file in the directory directly, respectively).
	 * 
	 * 
	 * @param userName of the user submitting this job
	 * @param jobConfiguration
	 * @param jobDescription
	 * @throws IOException if writing the file was not successful 
	 */
	void submitJobToMallob(String userName, 
			JobConfiguration jobConfiguration, 
			JobDescription jobDescription) throws IOException;
	
	/**
	 * Abort a running job from Mallob, using the mallob API : 
	 * create a .json with the necessary parameters and place it in a client-input directory.
	 * 
	 * @param runningJobID which has been assigned by mallob. This job will be aborted 
	 * @throws IOException if writing the file was not successful 
	 */
	void abortJob(int runningJobID) throws IOException;
}
