package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import edu.kit.fallob.database.EventDao;
import edu.kit.fallob.mallobio.outputupdates.Event;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 * 
 * Purpouse of this class is to listen for events 
 * and store them in the database 
 *
 */
public class EventListener implements OutputLogLineListener {
	
	
	private EventDao eventDao;
	
	
	public EventListener(EventDao eventDao) {
		this.eventDao = eventDao;
	}

	@Override
	public void processLine(String line) {
		if (Event.isEvent(line)) {
			eventDao.save(new Event(line));
		}
	}

}
