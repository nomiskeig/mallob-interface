package edu.kit.fallob.backend.database;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.DatabaseConnectionFactory;
import edu.kit.fallob.database.WarningDao;
import edu.kit.fallob.mallobio.outputupdates.Warning;
import edu.kit.fallob.springConfig.FallobException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public class WarningDaoTests {
    private static final String TEST_LOG_LINE = "Test warning";
    private static final String SECOND_TEST_LOG_LINE = "Second Test warning";

    private String databasePath;
    private Connection connection;
    private WarningDao warningDao;

    @BeforeEach
    public void setupEach() throws FallobException {
        this.databasePath = TestUtility.createDatabaseCopy();

        this.connection = TestUtility.getConnection(this.databasePath);

        try(MockedStatic<DatabaseConnectionFactory> factoryMock = Mockito.mockStatic(DatabaseConnectionFactory.class)) {
            factoryMock.when(DatabaseConnectionFactory::getConnection).thenReturn(this.connection);

            DaoFactory daoFactory = new DaoFactory();
            this.warningDao = daoFactory.getWarningDao();
        }
    }

    @Test
    public void testSaveAndGet() throws FallobException {
        Warning testWarning = new Warning(TEST_LOG_LINE);

        this.warningDao.save(testWarning);

        List<Warning> warnings = this.warningDao.getAllWarnings();

        Assertions.assertEquals(1, warnings.size());

        Warning returnWarning = warnings.get(0);
        Assertions.assertEquals(testWarning.getLogLine(), returnWarning.getLogLine());
    }

    @Test
    public void testSaveAndGetMultiple() throws FallobException {
        Warning testWarning1 = new Warning(TEST_LOG_LINE);
        Warning testWarning2 = new Warning(SECOND_TEST_LOG_LINE);

        this.warningDao.save(testWarning1);
        this.warningDao.save(testWarning2);

        List<Warning> warnings = this.warningDao.getAllWarnings();

        Assertions.assertEquals(warnings.size(), 2);

        Warning returnWarning1 = warnings.get(0);
        Warning returnWarning2 = warnings.get(1);

        Assertions.assertEquals(testWarning1.getLogLine(), returnWarning1.getLogLine());
        Assertions.assertEquals(testWarning2.getLogLine(), returnWarning2.getLogLine());
    }

    @Test
    public void testRemovalSingle() throws FallobException {
        Warning testWarning = new Warning(TEST_LOG_LINE);

        this.warningDao.save(testWarning);

        List<Warning> warningsBeforeDeletion = this.warningDao.getAllWarnings();
        Assertions.assertEquals(1, warningsBeforeDeletion.size());

        this.warningDao.removeAllWarningsBeforeTime(LocalDateTime.now());

        List<Warning> warningsAfterDeletion = this.warningDao.getAllWarnings();
        Assertions.assertEquals(0, warningsAfterDeletion.size());
    }

    @Test
    public void testRemovalMultiple() throws FallobException, InterruptedException {
        Warning testWarning1 = new Warning(TEST_LOG_LINE);
        Warning testWarning2 = new Warning(SECOND_TEST_LOG_LINE);

        this.warningDao.save(testWarning1);
        Thread.sleep(1000);
        LocalDateTime currentTime = LocalDateTime.now();
        Thread.sleep(1000);
        this.warningDao.save(testWarning2);

        List<Warning> warningsBeforeDeletion = this.warningDao.getAllWarnings();
        Assertions.assertEquals(2, warningsBeforeDeletion.size());

        this.warningDao.removeAllWarningsBeforeTime(currentTime);

        List<Warning> warningsAfterDeletion = this.warningDao.getAllWarnings();
        Assertions.assertEquals(1, warningsAfterDeletion.size());

        Warning returnWarning = warningsAfterDeletion.get(0);
        Assertions.assertEquals(SECOND_TEST_LOG_LINE, returnWarning.getLogLine());
    }


    @AfterEach
    public void cleanup() {
        TestUtility.removeDatabaseCopy(this.databasePath);
        this.warningDao = null;
        this.connection = null;
        this.databasePath = null;
    }
}
