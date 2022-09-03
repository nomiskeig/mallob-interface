package edu.kit.fallob.database;

import edu.kit.fallob.mallobio.outputupdates.Event;
import edu.kit.fallob.springConfig.FallobException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * an interface that represents a data access object for the Event class.
 * it is responsible for the persistent storage of the events that happen in mallob
 * @author Valentin Schenk
 * @version 1.0
 */
public interface EventDao {
    /**
     * saves the given event persistently
     * @param event the object that should be saved
     * @throws FallobException if an error occurs in the database
     */
    public void save(Event event) throws FallobException;

    /**
     * removes all events from the persistent storage, whose time attributes point to an earlier point in time
     * than the given timestamp
     * @param time the point in time from that on all Event objects should be removed
     * @throws FallobException if an error occurs in the database
     */
    public void removeEventsBeforeTime(LocalDateTime time) throws FallobException;

    /**
     * returns all events that are active at the given point in time
     * @param time the point in time from where the events should be returned
     * @return list of Event objects that represent the active events
     * @throws FallobException if an error occurs in the database
     */
    public List<Event> getEventsByTime(LocalDateTime time) throws FallobException;

    /**
     * returns a list of all events that happened in the given timespan
     * @param startTime the starting point of the timespan
     * @param endTime the ending point of the timespan
     * @return list of Event objects that represent the events
     * @throws FallobException if an error occurs in the database
     */
    public List<Event> getEventsBetweenTime(LocalDateTime startTime, LocalDateTime endTime) throws FallobException;

    /**
     * returns the time of the earliest stored event
     * @return the time of the event or if no event could be found
     * @throws FallobException if an error occurred in the database
     */
    public LocalDateTime getTimeOfFirstEvent() throws FallobException;
}
