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
public class EventListener implements OutputLogLineListener, BufferFunction<Event> {
	
	
	private EventDao eventDao;
	private Buffer<Event> eventBuffer;
	
	
	public EventListener(EventDao eventDao) {
		this.eventDao = eventDao;
		this.eventBuffer = new Buffer<>(this);
	}

	@Override
	public void processLine(String line) {
		if (Event.isEvent(line)) {
			Event e = new Event(line);
			eventBuffer.tryToExecuteBufferFunciton(e);
		}
		
		//try to save buffered events
		eventBuffer.retryBufferedFunction();
	}
	
	
	@Override
	public boolean bufferFunction(Event outputUpdate) {
		int jobID = convertJobID(outputUpdate.getMallobJobID());
		if (jobID != -1) {
			//set job-id and save to mallob
			outputUpdate.setJobID(jobID);
			try {
				eventDao.save(outputUpdate);
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
