package edu.kit.fallob.database;

import edu.kit.fallob.configuration.FallobConfiguration;

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

    //string that is used to get the database path into the right format for jdbc
    private static final String DATABASE_PATH_FORMAT = "jdbc:h2:%s;IFEXISTS=TRUE";
    //the username for the database
    private static final String DATABASE_USER = "fallob";
    //the password for the database
    private static final String DATABASE_PASSWORD = "";


    /**
     * establishes the connection to the database and returns the Connection object
     * @return the new Connection object
     */
    public static Connection getConnection() {
        FallobConfiguration configuration = FallobConfiguration.getInstance();

        String path = String.format(DATABASE_PATH_FORMAT, configuration.getDatabaseBasePath());

        try {
            return DriverManager.getConnection(path, DATABASE_USER, DATABASE_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
