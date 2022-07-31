package edu.kit.fallob.dataobjects;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.EventDao;
import edu.kit.fallob.mallobio.outputupdates.Event;

import java.time.LocalDateTime;
import java.util.List;

public class SystemState {
	
	private List<Event> systemState;
	
	
	public SystemState(LocalDateTime time) {
		DaoFactory daoFactory = new DaoFactory();
		EventDao eventDao = daoFactory.getEventDao();
		this.systemState = eventDao.getEventsByTime(time);
	}
	
	public List<Event> getSystemState() {
		return systemState;
	}
	
	public void setSystemState(List<Event> systemState) {
		this.systemState = systemState;
	}
}
