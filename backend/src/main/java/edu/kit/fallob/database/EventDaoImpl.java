package edu.kit.fallob.database;

import edu.kit.fallob.mallobio.outputupdates.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * class that responsible for storing the events from mallob in the database
 * @author Valentin Schenk
 * @version 1.0
 */
public class EventDaoImpl implements EventDao{

    private static final String INSERT_STATEMENT = "INSERT INTO event (jobId, rank, time, load, treeIndex)"
                                                    + " VALUES (?, ?, ?, ?, ?)";

    private final Connection conn;

    /**
     * constructor of the class
     */
    public EventDaoImpl() {
        this.conn = DatabaseConnectionFactory.getConnection();
    }
    /**
     * saves the given event in the database
     * @param event the object that should be saved
     */
    @Override
    public void save(Event event) {
        try {
            PreparedStatement statement = conn.prepareStatement(INSERT_STATEMENT);

            statement.setInt(1, event.getJobID());
            statement.setInt(2, event.getProcessID());
            //TODO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * removes all events from the database, whose time attributes point to an earlier point in time
     * than the given timestamp
     * @param time the point in time from that on all Event objects should be removed
     */
    @Override
    public void removeEventsBeforeTime(LocalDateTime time) {

    }

    /**
     * returns all events that are active at the given point in time
     * @param time the point in time from where the events should be returned
     * @return list of Event objects that represent the active events
     */
    @Override
    public List<Event> getEventsByTime(LocalDateTime time) {
        return null;
    }

    /**
     * returns a list of all events that happened in the given timespan
     * @param startTime the starting point of the timespan
     * @param endTime the ending point of the timespan
     * @return list of Event objects that represent the events
     */
    @Override
    public List<Event> getEventsBetweenTime(LocalDateTime startTime, LocalDateTime endTime) {
        return null;
    }

    /**
     * returns the time of the earliest stored event
     * @return the time of the event
     */
    @Override
    public LocalDateTime getTimeOfFirstEvent() {
        return null;
    }
}
