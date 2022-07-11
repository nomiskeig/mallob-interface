package edu.kit.fallob.dataobjects;

import edu.kit.fallob.mallobio.outputupdates.Event;

import java.util.List;

public class SystemState {
	private List<Event> systemState;
	
	public List<Event> getSystemState() {
		return systemState;
	}
	
	public void setSystemState(List<Event> systemState) {
		this.systemState = systemState;
	}
}
