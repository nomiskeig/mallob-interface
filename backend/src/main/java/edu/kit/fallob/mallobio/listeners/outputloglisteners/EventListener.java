package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import java.util.LinkedList;
import java.util.Queue;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.EventDao;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.mallobio.outputupdates.Event;
import edu.kit.fallob.springConfig.FallobException;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 * 
 *          Purpouse of this class is to listen for events
 *          and store them in the database
 *
 */
public class EventListener implements OutputLogLineListener, BufferFunction<Event> {

    private EventDao eventDao;
    private JobDao jobDao;
    private Buffer<Event> eventBuffer;

    public EventListener(EventDao eventDao, JobDao jobDao) {
        this.eventDao = eventDao;
        this.jobDao = jobDao;
        this.eventBuffer = new Buffer<>(this);
    }

    @Override
    public synchronized void processLine(String line) {
        if (Event.isEvent(line)) {
            Event e = new Event(line);
            eventBuffer.bufferObject(e);
        }

        // try to save buffered events
        eventBuffer.retryBufferedFunction(false);
    }

    @Override
    public synchronized boolean bufferFunction(Event outputUpdate) {
        int jobID = 0;
        try {
            jobID = this.jobDao.getJobIdByMallobId(outputUpdate.getMallobJobID());
        } catch (FallobException e) {
            e.printStackTrace();
            System.out.println("An error occurred while accessing the database");
        }
        if (jobID > 0) {
            // set job-id and save to mallob
            outputUpdate.setJobID(jobID);
            try {
                eventDao.save(outputUpdate);
            } catch (FallobException e) {
                e.printStackTrace();
                System.out.println("Event could not be saved : " + e.getMessage());
            }
            return true;
        }
        return false;
    }

}
