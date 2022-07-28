package edu.kit.fallob.backend.database;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.DatabaseConnectionFactory;
import edu.kit.fallob.database.WarningDao;
import edu.kit.fallob.mallobio.outputupdates.Warning;
import edu.kit.fallob.springConfig.FallobException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class WarningDaoTests {
    private static final String JDBC_FORMAT = "jdbc:h2:%s;IFEXISTS=TRUE";
    private static final String TEST_LOG_LINE = "Test warning";
    private static final String DATABASE_USER = "fallob";
    private static final String DATABASE_PASSWORD = "";
    private String databasePath;

    private Connection connection;
    private WarningDao warningDao;

    @BeforeEach
    public void setupEach() throws FallobException {
        this.databasePath = TestUtility.createDatabaseCopy();
        String jdbcPath = String.format(JDBC_FORMAT, this.databasePath);

        try {
            this.connection = DriverManager.getConnection(jdbcPath, DATABASE_USER, DATABASE_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        DaoFactory daoFactory = new DaoFactory();
        this.warningDao = daoFactory.getWarningDao();
    }

    @Test
    public void testSaveAndGet() throws FallobException {
        try(MockedStatic<DatabaseConnectionFactory> factoryMock = Mockito.mockStatic(DatabaseConnectionFactory.class)) {
            factoryMock.when(DatabaseConnectionFactory::getConnection).thenReturn(this.connection);

            Warning testWarning = new Warning(TEST_LOG_LINE);

            this.warningDao.save(testWarning);

            List<Warning> warnings = this.warningDao.getAllWarnings();

            Assertions.assertEquals(warnings.size(), 1);
            Assertions.assertEquals(warnings.get(0), testWarning);
        }
    }

    @AfterEach
    public void cleanup() {
        TestUtility.removeDatabaseCopy(this.databasePath);
    }
}
