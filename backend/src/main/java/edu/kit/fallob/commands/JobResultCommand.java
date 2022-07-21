package edu.kit.fallob.commands;

import edu.kit.fallob.dataobjects.JobResult;
import edu.kit.fallob.springConfig.FallobException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobResultCommand {
	
	public JobResult getSingleJobResult(String username, int jobID) throws FallobException {
		return null;
	}
	
	public List<JobResult> getMultipleJobResult(String username, List<Integer> jobIDs) throws FallobException {
		return null;
	}
	
	public List<JobResult> getAllJobResult(String username) throws FallobException {
		return null;
	}

}
