package edu.kit.fallob.commands;


import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.springConfig.FallobException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobSubmitCommands {
	
	
	public int submitJobWithDescriptionInclusive(String username, JobDescription jobdescription, JobConfiguration jobConfiguration) throws FallobException {
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
