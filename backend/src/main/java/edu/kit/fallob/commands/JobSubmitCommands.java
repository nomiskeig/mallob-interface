package edu.kit.fallob.commands;


import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;

public class JobSubmitCommands {
	
	
	public int submitJobWithDescription(String username, JobDescription jobdescription, JobConfiguration jobConfiguration) {
		return 0;
	}
	
	public int submitJobWithDescriptionID(String username, int jobdescriptionID, JobConfiguration jobConfiguration) {
		return 0;
	}
	
	public int restartCanceledJob(String username, int jobID) {
		return 0;
	}
	
	public int saveJobDescription(String username, JobDescription jobdescription) {
		return 0;
	}

}
