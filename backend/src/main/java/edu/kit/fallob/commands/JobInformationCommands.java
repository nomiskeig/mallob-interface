package edu.kit.fallob.commands;

import edu.kit.fallob.dataobjects.JobInformation;
import edu.kit.fallob.springConfig.FallobException;

import java.util.List;


public class JobInformationCommands {
	
	
	public JobInformation getSingleJobInformation(String username, int jobID) throws FallobException {
		return null;
	}
	
	public List<JobInformation> getMultipleJobInformation(String username, List<Integer> jobIDs) throws FallobException {
		return null;
	}
	
	public List<JobInformation> getAllJobInformation(String username) throws FallobException {
		return null;
	}
	
	public List<JobInformation> getAllGlobalJobInformation(String username) throws FallobException {
		return null;
	}

}
