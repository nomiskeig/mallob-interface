package edu.kit.fallob.commands;

import java.util.List;

import edu.kit.fallob.dataobjects.SystemState;
import edu.kit.fallob.mallobio.outputupdates.Event;
import edu.kit.fallob.mallobio.outputupdates.Warning;

public class MallobCommands {
	
	public SystemState getSystemState(String time) {
		return null;
	}
	
	public List<Event> getEvents(String lowerBound, String upperBound){
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
