package edu.kit.fallob.backend.database;

import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.database.DatabaseConnectionFactory;
import edu.kit.fallob.springConfig.FallobException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class DatabaseConnectionTests {
    private static final String DB_PATH = "./src/test/resources/database/fallobTestDatabase";
    private static final String DB_USERNAME = "fallob";
    private static final String DB_PASSWORD = "";

    FallobConfiguration config = Mockito.spy(FallobConfiguration.getInstance());


    /**
     * test a successful connection to the database
     */
    @Test
    public void testSuccessfulConnection() throws FallobException {
        Mockito.when(config.getDatabaseBasePath()).thenReturn(DB_PATH);
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
}
