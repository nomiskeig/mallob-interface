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
    private static final String DATABASE_ERROR = "An error occurred while accessing the database.";
    private static final String DATABASE_NOT_FOUND = "Error, the requested entry couldn't be found.";

    private static final String INSERT_STATEMENT = "INSERT INTO event (jobId, rank, time, load, treeIndex)"
                                                    + " VALUES (?, ?, ?, ?, ?)";
    private static final String REMOVE_BEFORE_TIME = "DELETE FROM event WHERE time < ?";
    private static final String GET_LOAD_1_EVENTS = "SELECT * FROM event WHERE load = true AND time <= ?";
    private static final String GET_LOAD_0_EVENTS = "SELECT * FROM event WHERE load = false AND time <= ?";
    private static final String GET_BETWEEN_TIME = "SELECT * FROM event WHERE time >= ? AND time < ?";
    private static final String TIME_OF_FIRST_EVENT = "SELECT time FROM event ORDER BY time ASC";

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
            statement.setObject(3, event.getTime());
            statement.setBoolean(4, event.isLoad());
            statement.setInt(5, event.getTreeIndex());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR);
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
            throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR, e);
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
        try {
            //get all events that happened before the given time split into load 1 and load 0 events
            PreparedStatement statementLoad1 = this.conn.prepareStatement(GET_LOAD_1_EVENTS);
            statementLoad1.setString(1, time.toString());
            List<Event> load1Events = this.getEvents(statementLoad1);

            PreparedStatement statementLoad0 = this.conn.prepareStatement(GET_LOAD_0_EVENTS);
            statementLoad0.setString(1, time.toString());
            List<Event> load0Events = this.getEvents(statementLoad0);

            //iterate over all the load 0 events
            for (Event event: load0Events) {
                int process = event.getProcessID();
                int jobId = event.getJobID();

                //search for the matching load1 event and remove it from the list
                for (Event load1Event: load1Events) {
                    if (process == load1Event.getProcessID() && jobId == load1Event.getJobID()) {
                        load1Events.remove(load1Event);
                        break;
                    }
                }
            }

            return load1Events;

        } catch (SQLException e) {
            throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR, e);
        }
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
        try {
            PreparedStatement statement = this.conn.prepareStatement(GET_BETWEEN_TIME);
            statement.setString(1, startTime.toString());
            statement.setString(2, endTime.toString());

            return this.getEvents(statement);
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR, e);
        }
    }

    /**
     * returns the time of the earliest stored event
     * @return the time of the event or null if no event could be found
     * @throws FallobException if an error occurred in the database
     */
    @Override
    public LocalDateTime getTimeOfFirstEvent() throws FallobException {
        try {
            PreparedStatement statement = this.conn.prepareStatement(TIME_OF_FIRST_EVENT);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getTimestamp(1).toLocalDateTime();
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR, e);
        }

    }

    /**
     * returns a list of events based on a given prepared statement
     * @param statement the prepared statement that defines which events should be returned
     * @return the list of events
     * @throws FallobException if an error occurs while accessing the database
     */
    private List<Event> getEvents(PreparedStatement statement) throws FallobException {
        List<Event> events = new ArrayList<>();
        try {
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                //start at index 2 because index 1 is the eventId which is unnecessary
                int jobId = result.getInt(2);
                int processId = result.getInt(3);
                LocalDateTime time = result.getTimestamp(4).toLocalDateTime();
                boolean load = result.getBoolean(5);
                int treeIndex = result.getInt(6);

                Event event = new Event(processId, treeIndex, jobId, load, time);
                events.add(event);
            }

            return events;
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR, e);
        }
    }
}
