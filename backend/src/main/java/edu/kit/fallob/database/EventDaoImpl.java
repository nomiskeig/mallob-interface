package edu.kit.fallob.database;

import edu.kit.fallob.mallobio.outputupdates.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * class that responsible for storing the events from mallob in the database
 * @author Valentin Schenk
 * @version 1.0
 */
public class EventDaoImpl implements EventDao{

    private static final String INSERT_STATEMENT = "INSERT INTO event (jobId, rank, time, load, treeIndex)"
                                                    + " VALUES (?, ?, ?, ?, ?)";
    private static final String REMOVE_BEFORE_TIME = "DELETE FROM event WHERE time < ?";
    private static final String GET_BETWEEN_TIME = "SELECT * FROM event WHERE time > ? AND time <= ?";
    private static final String TIME_OF_FIRST_EVENT = "SELECT time FROM event WHERE MIN(time)";

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
        try {
            PreparedStatement statement = this.conn.prepareStatement(REMOVE_BEFORE_TIME);
            statement.setString(1, time.toString());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * returns all events that are active at the given point in time
     * @param time the point in time from where the events should be returned
     * @return list of Event objects that represent the active events
     */
    @Override
    public List<Event> getEventsByTime(LocalDateTime time) {
        //TODO
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
        List<Event> events = new ArrayList<>();
        try {
            PreparedStatement statement = this.conn.prepareStatement(GET_BETWEEN_TIME);
            statement.setString(1, startTime.toString());
            statement.setString(2, endTime.toString());

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Event event = this.constructEvent(result);
                events.add(event);
            }

            return events;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * returns the time of the earliest stored event
     * @return the time of the event
     */
    @Override
    public LocalDateTime getTimeOfFirstEvent() {
        try {
            PreparedStatement statement = this.conn.prepareStatement(TIME_OF_FIRST_EVENT);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getTimestamp(1).toLocalDateTime();
            } else {
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * contructs a new Event object from the ResultSet from the database
     * @param result the ResultSet that contains the information necessary to construct the event
     * @return the constructed Event object
     */
    private Event constructEvent(ResultSet result) {
        //TODO: missing constructor in Event class
        return null;
    }
}
