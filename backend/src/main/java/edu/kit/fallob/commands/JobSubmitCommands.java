package edu.kit.fallob.commands;


import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.springConfig.FallobException;

import java.util.List;

public class JobSubmitCommands {
	
	
	public int submitJobWithDescriptionFile(String username, JobDescription jobdescription, JobConfiguration jobConfiguration) throws FallobException {
		return 0;
	}

	public int submitJobWithDescriptionString(String username, List<String> jobdescription, JobConfiguration jobConfiguration) throws FallobException {
		return 0;
	}
	
	public int submitJobWithDescriptionID(String username, int jobdescriptionID, JobConfiguration jobConfiguration) throws FallobException {
		return 0;
	}
	
	public int restartCanceledJob(String username, int jobID) throws FallobException {
		return 0;
	}
	
	public int saveJobDescription(String username, JobDescription jobdescription) throws FallobException {
		return 0;
	}

}
