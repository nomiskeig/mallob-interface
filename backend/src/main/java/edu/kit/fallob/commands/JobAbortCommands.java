package edu.kit.fallob.commands;

import edu.kit.fallob.springConfig.FallobException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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
