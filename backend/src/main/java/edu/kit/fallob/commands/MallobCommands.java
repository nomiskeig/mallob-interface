package edu.kit.fallob.commands;

import java.util.List;

import edu.kit.fallob.dataobjects.SystemState;
import edu.kit.fallob.mallobio.outputupdates.Event;
import edu.kit.fallob.mallobio.outputupdates.Warning;
import edu.kit.fallob.springConfig.FallobException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MallobCommands {
	
	public SystemState getSystemState(String time) throws FallobException {
		return null;
	}
	
	public List<Event> getEvents(String lowerBound, String upperBound) throws FallobException {
		return null;
	}
	
	public List<Warning> getWarnings(){
		return null;
	}
	
	public boolean stopMallob() {
		return false;
	}
	
	public boolean startMallob(String params) {
		return false;
	}

}
