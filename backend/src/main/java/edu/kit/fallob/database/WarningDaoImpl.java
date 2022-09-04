package edu.kit.fallob.database;

import edu.kit.fallob.mallobio.outputupdates.Warning;
import edu.kit.fallob.springConfig.FallobException;
import org.springframework.http.HttpStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * class that is responsible for storing the warnings from Mallob in the database
 * @author Valentin Schenk
 * @version 1.0
 */
public class WarningDaoImpl implements WarningDao{
    //error message that is returned if an error occurs
    private static final String DATABASE_ERROR = "An error occurred while accessing the database";

    //the SQL statement for inserting a new warning
    private static final String INSERT_STATEMENT = "INSERT INTO warning (time, message) VALUES (?, ?)";
    //the SQL statement for removing the old warnings
    private static final String DELETE_STATEMENT = "DELETE FROM warning WHERE time < ?";
    //the SQL query to get all warning in the database
    private static final String GET_QUERY = "SELECT * FROM warning";
    //the index of the result that describes where the message of the warning is stored
    private static final int MESSAGE_INDEX = 3;
    private static final int TIME_INDEX = 2;

    private final Connection conn;

    /**
     * constructor of the class
     * @throws FallobException if an error occurs while accessing the database
     */
    public WarningDaoImpl() throws FallobException {
        this.conn = DatabaseConnectionFactory.getConnection();
    }

    /**
     * saves the given warning persistently in the database
     * @param warning the warning object that contains the data that should be saved
     * @throws FallobException if an error occurs while accessing the database
     */
    @Override
    public void save(Warning warning) throws FallobException {
        try {
            PreparedStatement statement = this.conn.prepareStatement(INSERT_STATEMENT);

            statement.setObject(1, warning.getTime());
            statement.setString(2, warning.getMessage());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR);
        }
    }

    /**
     * removes all warnings that were stored before the give point in time
     * @param time the point in time that defines which warnings get removed
     * @throws FallobException if an error occurs while accessing the database
     */
    @Override
    public void removeAllWarningsBeforeTime(LocalDateTime time) throws FallobException {
        try {
            PreparedStatement statement = this.conn.prepareStatement(DELETE_STATEMENT);

            statement.setString(1, time.toString());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR);
        }

    }

    /**
     * returns all warnings that are currently stored
     * @return a list with all warnings
     * @throws FallobException if an error occurs while accessing the database
     */
    @Override
    public List<Warning> getAllWarnings() throws FallobException {
        List<Warning> warnings = new ArrayList<>();

        try {
            Statement statement = this.conn.createStatement();

            ResultSet resultSet = statement.executeQuery(GET_QUERY);

            while (resultSet.next()) {
                LocalDateTime time  = resultSet.getTimestamp(TIME_INDEX).toLocalDateTime();
                String message = resultSet.getString(MESSAGE_INDEX);
                Warning warning = new Warning(message, time);
                warnings.add(warning);
            }
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR);
        }

        return warnings;
    }
}
