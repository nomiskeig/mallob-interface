package edu.kit.fallob.commands;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.EventDao;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.database.WarningDao;
import edu.kit.fallob.dataobjects.SystemState;
import edu.kit.fallob.mallobio.outputupdates.Event;
import edu.kit.fallob.mallobio.outputupdates.Warning;

public class MallobCommands {
	
	private DaoFactory daoFactory;
	private EventDao eventDao;
	private WarningDao warningDao;
	
	public MallobCommands() {
		daoFactory = new DaoFactory();
		eventDao = daoFactory.getEventDao();
		warningDao = daoFactory.getWarningDao();
	}
	
	public SystemState getSystemState(String time) {
		LocalDateTime formattedTime = LocalDateTime.parse(time);
		return new SystemState(formattedTime);
	}
	
	public List<Event> getEvents(String lowerBound, String upperBound) {
		LocalDateTime formattedLowerBound = LocalDateTime.parse(lowerBound);
		LocalDateTime formattedUpperBound = LocalDateTime.parse(upperBound);
		return eventDao.getEventsBetweenTime(formattedLowerBound, formattedUpperBound);
	}
	
	public List<Warning> getWarnings(){
		return warningDao.getAllWarnings();
	}
	
	public boolean stopMallob() {
		return false;
	}
	
	public boolean startMallob(String params) {
		return false;
	}

}
