package edu.kit.fallob.commands;

import edu.kit.fallob.springConfig.FallobException;

import java.util.List;

public class JobAbortCommands {

	
	public boolean abortSingleJob(String username, int jobID) throws FallobException {
		return false;
	}
	
	public List<Integer> abortMultipleJobs(String username, List<Integer> jobID) throws FallobException {
		return null;
	}
	
	public List<Integer> abortAllJobs(String username) throws FallobException {
		return null;
	}
	
	public List<Integer> abortAlGlobalJob(String username) throws FallobException {
		return null;
	}

}
