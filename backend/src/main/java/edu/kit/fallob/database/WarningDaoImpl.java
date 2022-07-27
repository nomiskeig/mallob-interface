package edu.kit.fallob.database;

import edu.kit.fallob.mallobio.outputupdates.Warning;
import edu.kit.fallob.springConfig.FallobException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * class that is responsible for storing the warnings from Mallob in the database
 * @author Valentin Schenk
 * @version 1.0
 */
public class WarningDaoImpl implements WarningDao{
    //the SQL statement for inserting a new warning
    private static final String INSERT_STATEMENT = "INSERT INTO warning (time, message) VALUES (?, ?)";
    //the SQL statement for removing the old warnings
    private static final String DELETE_STATEMENT = "DELETE FROM warning WHERE time < '?'";
    //the SQL query to get all warning in the database
    private static final String GET_QUERY = "SELECT * FROM warning";
    //the index of the result that describes where the message of the warning is stored
    private static final int MESSAGE_INDEX = 3;

    private final Connection conn;

    /**
     * constructor of the class
     */
    public WarningDaoImpl() throws FallobException {
        this.conn = DatabaseConnectionFactory.getConnection();
    }

    /**
     * saves the given warning persistently in the database
     * @param warning the warning object that contains the data that should be saved
     */
    @Override
    public void save(Warning warning) {
        try {
            PreparedStatement statement = this.conn.prepareStatement(INSERT_STATEMENT);

            statement.setObject(1, LocalDateTime.now());
            statement.setString(2, warning.getLogLine());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * removes all warnings that were stored before the give point in time
     * @param time the point in time that defines which warnings get removed
     */
    @Override
    public void removeAllWarningsBeforeTime(LocalDateTime time) {
        try {
            PreparedStatement statement = this.conn.prepareStatement(DELETE_STATEMENT);

            statement.setString(1, time.toString());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * returns all warnings that are currently stored
     * @return a list with all warnings
     */
    @Override
    public List<Warning> getAllWarnings() {
        List<Warning> warnings = new ArrayList<>();

        try {
            Statement statement = this.conn.createStatement();

            ResultSet resultSet = statement.executeQuery(GET_QUERY);

            while (resultSet.next()) {
                Warning warning = new Warning(resultSet.getString(MESSAGE_INDEX));
                warnings.add(warning);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return warnings;
    }
}
