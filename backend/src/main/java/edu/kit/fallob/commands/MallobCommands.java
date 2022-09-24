package edu.kit.fallob.commands;

import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.EventDao;
import edu.kit.fallob.database.WarningDao;
import edu.kit.fallob.dataobjects.SystemState;
import edu.kit.fallob.mallobio.outputupdates.Event;
import edu.kit.fallob.mallobio.outputupdates.Warning;
import edu.kit.fallob.springConfig.FallobException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class MallobCommands {
	
	private DaoFactory daoFactory;
	private EventDao eventDao;
	private WarningDao warningDao;
	private FallobConfiguration fallobConfiguration;

	private static final String EVENT_MESSAGE = "No Events will be found for this time value, as it is incorrect";
	
	public MallobCommands() throws FallobException{
		try {
			daoFactory = new DaoFactory();
			eventDao = daoFactory.getEventDao();
			warningDao = daoFactory.getWarningDao();
			fallobConfiguration = FallobConfiguration.getInstance();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
	public SystemState getSystemState(LocalDateTime time) throws FallobException {
		if (time.isBefore(fallobConfiguration.getStartTime()) || time.isAfter(LocalDateTime.now(ZoneOffset.UTC))) {
			throw new FallobException(HttpStatus.NOT_FOUND, EVENT_MESSAGE);
		}
		return new SystemState(time);
	}
	
	public List<Event> getEvents(LocalDateTime lowerBound, LocalDateTime upperBound) throws FallobException {
		if (lowerBound.isBefore(fallobConfiguration.getStartTime()) || lowerBound.isAfter(LocalDateTime.now(ZoneOffset.UTC))
		|| upperBound.isBefore(fallobConfiguration.getStartTime()) || upperBound.isAfter(LocalDateTime.now(ZoneOffset.UTC))) {
			throw new FallobException(HttpStatus.NOT_FOUND, EVENT_MESSAGE);
		}
		return eventDao.getEventsBetweenTime(lowerBound, upperBound);
	}
	
	public List<Warning> getWarnings() throws FallobException {
		return warningDao.getAllWarnings();
	}
	
	public boolean stopMallob() {
		return false;
	}
	
	public boolean startMallob(String params) {
		return false;
	}

}
