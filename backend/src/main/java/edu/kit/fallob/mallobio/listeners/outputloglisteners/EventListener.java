package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import java.util.LinkedList;
import java.util.Queue;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.EventDao;
import edu.kit.fallob.mallobio.outputupdates.Event;
import edu.kit.fallob.springConfig.FallobException;

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
	private Queue<Event> unsavedEvents;
	
	
	public EventListener(EventDao eventDao) {
		this.eventDao = eventDao;
		unsavedEvents = new LinkedList<>();
	}

	@Override
	public void processLine(String line) {
		if (Event.isEvent(line)) {
			Event e = new Event(line);
			tryToStoreEvent(e);
		}
		
		//try to save buffered events
		retrySavingBufferedEvents();
	}
	
	
	/**
	 * Runs through all buffered events and tries to re-store them to the database 
	 */
	private void retrySavingBufferedEvents() {
		if (unsavedEvents.size() == 0) {return;}
		int maxTries = unsavedEvents.size();
		Event event = unsavedEvents.poll();
		
		while(event != null && maxTries > 0) {
			tryToStoreEvent(event);
			maxTries--;
			event = unsavedEvents.poll();
		}
	}
	
	
	/*
	 * Tries to convert the mallob-id of the event to the fallob job-id 
	 * and if it fails, it buffers the event via bufferEvent(event)
	 */
	private void tryToStoreEvent(Event event) {
		if (!storeEvent(event)) {
			bufferEvent(event);
		} 
	}
	
	private void bufferEvent(Event event) {
		try {
			unsavedEvents.add(event);
		} catch(IllegalStateException e) {
			System.out.println("Event could not be added to buffering-queue : capacity overflow.");
		}
	}

	/**
	 * 
	 * @param e
	 * @return false if event could not be saved to databse, true if it did 
	 */
	private boolean storeEvent(Event event) {
		int jobID = convertJobID(event.getMallobJobID());
		if (jobID != -1) {
			//set job-id and save to mallob
			event.setJobID(jobID);
			try {
				eventDao.save(event);
			} catch (FallobException e) {
				System.out.println("Event could not be saved : " + e.getMessage());
			}
			return true;
		}
		return false;
	}
	

	/**
	 * Convert a mallobID into an internal fallob-ID
	 * @param mallobID
	 * @return internal fallob-ID, if conversion was successful, -1 if conversion failed
	 */
	private int convertJobID(int mallobID) {
		//convert the mallob Id into the correct job id
		try {
			return new DaoFactory().getJobDao().getJobIdByMallobId(mallobID);
		} catch (FallobException e) {
			return -1;
		}		
	}

}
