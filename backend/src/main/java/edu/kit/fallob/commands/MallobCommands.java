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
import edu.kit.fallob.springConfig.FallobException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
public class MallobCommands {
	
	private DaoFactory daoFactory;
	private EventDao eventDao;
	private WarningDao warningDao;
	
	public MallobCommands() throws FallobException{
		// TODO Until the data base is fully implemented, we catch the error so the program could be started - should we remove try-catch after that?
		try {
			daoFactory = new DaoFactory();
			eventDao = daoFactory.getEventDao();
			warningDao = daoFactory.getWarningDao();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
	public SystemState getSystemState(String time) throws FallobException {
		LocalDateTime formattedTime = LocalDateTime.parse(time);
		return new SystemState(formattedTime);
	}
	
	public List<Event> getEvents(String lowerBound, String upperBound) throws FallobException {
		LocalDateTime formattedLowerBound = LocalDateTime.parse(lowerBound);
		LocalDateTime formattedUpperBound = LocalDateTime.parse(upperBound);
		return eventDao.getEventsBetweenTime(formattedLowerBound, formattedUpperBound);
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
