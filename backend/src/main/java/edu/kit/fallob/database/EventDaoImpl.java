package edu.kit.fallob.database;

import edu.kit.fallob.mallobio.outputupdates.Event;
import edu.kit.fallob.springConfig.FallobException;
import org.springframework.http.HttpStatus;

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

    //error message that are returned if an error occurs
    private static final String DATABASE_ERROR = "An error occurred while accessing the database";
    private static final String DATABASE_NOT_FOUND = "Error, the requested ";

    private static final String INSERT_STATEMENT = "INSERT INTO event (jobId, rank, time, load, treeIndex)"
                                                    + " VALUES (?, ?, ?, ?, ?)";
    private static final String REMOVE_BEFORE_TIME = "DELETE FROM event WHERE time < ?";
    private static final String GET_BETWEEN_TIME = "SELECT * FROM event WHERE time > ? AND time <= ?";
    private static final String TIME_OF_FIRST_EVENT = "SELECT time FROM event WHERE MIN(time)";

    private final Connection conn;

    /**
     * constructor of the class
     * @throws FallobException if something goes wrong while connecting to the database
     */
    public EventDaoImpl() throws FallobException {
        this.conn = DatabaseConnectionFactory.getConnection();
    }
    /**
     * saves the given event in the database
     * @param event the object that should be saved
     * @throws FallobException if an error occurs in the database
     */
    @Override
    public void save(Event event) throws FallobException {
        try {
            PreparedStatement statement = conn.prepareStatement(INSERT_STATEMENT);

            statement.setInt(1, event.getJobID());
            statement.setInt(2, event.getProcessID());
            //TODO
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }

    /**
     * removes all events from the database, whose time attributes point to an earlier point in time
     * than the given timestamp
     * @param time the point in time from that on all Event objects should be removed
     * @throws FallobException if an error occurs in the database
     */
    @Override
    public void removeEventsBeforeTime(LocalDateTime time) throws FallobException {
        try {
            PreparedStatement statement = this.conn.prepareStatement(REMOVE_BEFORE_TIME);
            statement.setString(1, time.toString());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }

    /**
     * returns all events that are active at the given point in time
     * @param time the point in time from where the events should be returned
     * @return list of Event objects that represent the active events
     * @throws FallobException if an error occurs in the database
     */
    @Override
    public List<Event> getEventsByTime(LocalDateTime time) throws FallobException {
        //TODO
        return null;
    }

    /**
     * returns a list of all events that happened in the given timespan
     * @param startTime the starting point of the timespan
     * @param endTime the ending point of the timespan
     * @return list of Event objects that represent the events
     * @throws FallobException if an error occurs in the database
     */
    @Override
    public List<Event> getEventsBetweenTime(LocalDateTime startTime, LocalDateTime endTime) throws FallobException {
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
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }

    /**
     * returns the time of the earliest stored event
     * @return the time of the event
     * @throws FallobException if an error occurs in the database or if no event could be found
     */
    @Override
    public LocalDateTime getTimeOfFirstEvent() throws FallobException {
        try {
            PreparedStatement statement = this.conn.prepareStatement(TIME_OF_FIRST_EVENT);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getTimestamp(1).toLocalDateTime();
            } else {
                throw new FallobException(HttpStatus.NOT_FOUND, DATABASE_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
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
