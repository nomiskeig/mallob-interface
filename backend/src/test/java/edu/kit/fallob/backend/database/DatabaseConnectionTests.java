package edu.kit.fallob.backend.database;

import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.database.DatabaseConnectionFactory;
import edu.kit.fallob.springConfig.FallobException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class DatabaseConnectionTests {
    private static final String DB_USERNAME = "fallob";
    private static final String DB_PASSWORD = "";
    private String databasePath;

    FallobConfiguration config = Mockito.spy(FallobConfiguration.getInstance());


    @BeforeEach
    public void setup() {
        this.databasePath = TestUtility.createDatabaseCopy();
    }

    /**
     * test a successful connection to the database
     */
    @Test
    public void testSuccessfulConnection() throws FallobException {
        Mockito.when(config.getDatabaseBasePath()).thenReturn(this.databasePath);
        Mockito.when(config.getDataBaseUsername()).thenReturn(DB_USERNAME);
        Mockito.when(config.getDatabasePassword()).thenReturn(DB_PASSWORD);
        try (MockedStatic<FallobConfiguration> mockedConfig = Mockito.mockStatic(FallobConfiguration.class)) {
            mockedConfig.when(FallobConfiguration::getInstance).thenReturn(config);

            Assertions.assertNotNull(DatabaseConnectionFactory.getConnection());
        }

    }

    @Test
    public void testFailedConnection() {
        Mockito.when(config.getDatabaseBasePath()).thenReturn("");
        Mockito.when(config.getDataBaseUsername()).thenReturn(DB_USERNAME);
        Mockito.when(config.getDatabasePassword()).thenReturn(DB_PASSWORD);
        try (MockedStatic<FallobConfiguration> mockedConfig = Mockito.mockStatic(FallobConfiguration.class)) {
            mockedConfig.when(FallobConfiguration::getInstance).thenReturn(config);

            Assertions.assertThrows(FallobException.class, DatabaseConnectionFactory::getConnection);
        }
    }

    @AfterEach
    public void cleanup() {
        TestUtility.removeDatabaseCopy(this.databasePath);
        this.databasePath = null;
    }
}
