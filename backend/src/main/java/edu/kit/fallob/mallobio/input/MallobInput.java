package edu.kit.fallob.mallobio.input;

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
	 * 
	 * @param userName
	 * @param jobConfiguration
	 * @param jobDescription
	 */
	//void submitJobToMallob(Sting userName, JobConfiguration jobConfiguration, JobDescription jobDescription);
	
	/**
	 * Abort a running job from Mallob
	 * 
	 * @param runningJobID which has been assigned by mallob. This job will be aborted 
	 * @return true, if the command has been sucessfully delivered to mallob, 
	 * false if any errors occurred
	 */
	boolean abortJob(int runningJobID);
}
