package edu.kit.fallob.database;

import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.springConfig.FallobException;
import org.springframework.http.HttpStatus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class that is responsible for establishing the connection to the database and creating Connection objects
 * @author Valentin Schenk
 * @version 1.0
 */
public final class DatabaseConnectionFactory {

    /**
     * private constructor to prevent instantiation of the class
     */
    private DatabaseConnectionFactory() {
    }
    //the massage that gets sent if something goes wrong
    private static final String ERROR_MESSAGE = "Error, the connection to the database couldn't be established.";

    //string that is used to get the database path into the right format for jdbc
    private static final String DATABASE_PATH_FORMAT = "jdbc:h2:%s;IFEXISTS=TRUE";


    /**
     * establishes the connection to the database and returns the Connection object
     * @return the new Connection object
     */
    public static Connection getConnection() throws FallobException {
        FallobConfiguration configuration = FallobConfiguration.getInstance();

        String databasePath = configuration.getDatabaseBasePath();
        if (databasePath.endsWith(".mv.db")) {
           databasePath =  databasePath.substring(0, databasePath.length() - 6);
            
        }
        
        String path = String.format(DATABASE_PATH_FORMAT, databasePath);
        String username = configuration.getDataBaseUsername();
        String password = configuration.getDatabasePassword();

        try {
            return DriverManager.getConnection(path, username, password);
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE, e);
        }
    }
}
