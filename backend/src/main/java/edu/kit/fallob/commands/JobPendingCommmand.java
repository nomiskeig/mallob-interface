package edu.kit.fallob.commands;

import edu.kit.fallob.dataobjects.ResultMetaData;
import edu.kit.fallob.springConfig.FallobException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobPendingCommmand {
	
	public ResultMetaData waitForJob(String username, int jobID) throws FallobException {
		return null;
	}

}
