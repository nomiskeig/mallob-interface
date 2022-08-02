package edu.kit.fallob.dataobjects;

import edu.kit.fallob.commands.UserActionAuthentificater;
import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.EventDao;
import edu.kit.fallob.mallobio.outputupdates.Event;
import edu.kit.fallob.springConfig.FallobException;

import java.time.LocalDateTime;
import java.util.List;

public class SystemState {
	
	private List<Event> systemState;
	
	
	public SystemState(LocalDateTime time) throws FallobException {
		// TODO Until the data base is fully implemented, we catch the error so the program could be started - should we remove try-catch after that?
		try {
			DaoFactory daoFactory = new DaoFactory();
			EventDao eventDao = daoFactory.getEventDao();
			this.systemState = eventDao.getEventsByTime(time);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
	public List<Event> getSystemState() {
		return systemState;
	}
	
	public void setSystemState(List<Event> systemState) {
		this.systemState = systemState;
	}
}
