package edu.kit.fallob.backend.database;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.DatabaseConnectionFactory;
import edu.kit.fallob.database.EventDao;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.database.UserDao;
import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.dataobjects.NormalUser;
import edu.kit.fallob.dataobjects.SubmitType;
import edu.kit.fallob.dataobjects.User;
import edu.kit.fallob.mallobio.outputupdates.Event;
import edu.kit.fallob.springConfig.FallobException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class EventDaoTests {
    private static final String TEST_USERNAME = "test";

    private int descriptionId1;
    private int descriptionId2;
    private int testJobId1;
    private int testJobId2;
    private String databasePath;
    private Connection connection;
    private EventDao eventDao;

    @BeforeEach
    public void setup() throws FallobException {
        this.databasePath = TestUtility.createDatabaseCopy();
        this.connection = TestUtility.getConnection(this.databasePath);

        UserDao userDao;
        JobDao jobDao;
        try(MockedStatic<DatabaseConnectionFactory> factoryMock = Mockito.mockStatic(DatabaseConnectionFactory.class)) {
            factoryMock.when(DatabaseConnectionFactory::getConnection).thenReturn(this.connection);

            DaoFactory daoFactory = new DaoFactory();
            this.eventDao = daoFactory.getEventDao();
            userDao = daoFactory.getUserDao();
            jobDao = daoFactory.getJobDao();
        }

        User testUser = new NormalUser(TEST_USERNAME, "123", "test@test.com");
        userDao.save(testUser);

        JobDescription testDescription1 = new JobDescription(new ArrayList<>(), SubmitType.URL);
        this.descriptionId1 = jobDao.saveJobDescription(testDescription1, TEST_USERNAME);
        JobDescription testDescription2 = new JobDescription(new ArrayList<>(), SubmitType.EXCLUSIVE);
        this.descriptionId2 = jobDao.saveJobDescription(testDescription2, TEST_USERNAME);

        JobConfiguration config1 = new JobConfiguration("test1", 1.0, "SAT");
        config1.setDescriptionID(descriptionId1);
        JobConfiguration config2 = new JobConfiguration("test2", 0.5, "SAT");
        config2.setDescriptionID(descriptionId2);

        this.testJobId1 = jobDao.saveJobConfiguration(config1, TEST_USERNAME, 1);
        this.testJobId2 = jobDao.saveJobConfiguration(config2, TEST_USERNAME, 2);
    }

    @Test
    public void TestGetBetweenTime() throws FallobException, InterruptedException {
        Event testEvent1 = new Event(5, 0, this.testJobId1, true, LocalDateTime.now());
        this.eventDao.save(testEvent1);
        Thread.sleep(1000);
        LocalDateTime time = LocalDateTime.now();
        Thread.sleep(1000);
        Event testEvent2 = new Event(4, 1, testJobId2, true, LocalDateTime.now());
        this.eventDao.save(testEvent2);

        List<Event> eventsWholeTime = this.eventDao.getEventsBetweenTime(LocalDateTime.MIN, LocalDateTime.MAX);
        Assertions.assertEquals(2, eventsWholeTime.size());

        List<Event> eventsFirstHalf = this.eventDao.getEventsBetweenTime(LocalDateTime.MIN, time);
        Assertions.assertEquals(1, eventsFirstHalf.size());
        Assertions.assertEquals(this.testJobId1, eventsFirstHalf.get(0).getJobID());

        List<Event> eventsSecondHalf = this.eventDao.getEventsBetweenTime(time, LocalDateTime.MAX);
        Assertions.assertEquals(1, eventsSecondHalf.size());
        Assertions.assertEquals(this.testJobId2, eventsSecondHalf.get(0).getJobID());
    }

    @Test
    public void testRemoveEventsBetweenTime() throws FallobException, InterruptedException {
        Event testEvent1 = new Event(5, 0, this.testJobId1, true, LocalDateTime.now());
        this.eventDao.save(testEvent1);
        Thread.sleep(1000);
        LocalDateTime time = LocalDateTime.now();
        Thread.sleep(1000);
        Event testEvent2 = new Event(4, 1, testJobId2, true, LocalDateTime.now());
        this.eventDao.save(testEvent2);

        List<Event> eventsBeforeDeletion = this.eventDao.getEventsBetweenTime(LocalDateTime.MIN, LocalDateTime.MAX);
        Assertions.assertEquals(2, eventsBeforeDeletion.size());

        this.eventDao.removeEventsBeforeTime(time);

        List<Event> eventsAfterDeletion = this.eventDao.getEventsBetweenTime(LocalDateTime.MIN, LocalDateTime.MAX);

        Assertions.assertEquals(1, eventsAfterDeletion.size());
        Assertions.assertEquals(eventsAfterDeletion.get(0).getJobID(), this.testJobId2);
    }

    @Test
    public void testGetFirstEvent() throws FallobException, InterruptedException {
        LocalDateTime time1 = LocalDateTime.now();
        Event testEvent1 = new Event(5, 0, this.testJobId1, true, time1);
        this.eventDao.save(testEvent1);
        Thread.sleep(1000);
        LocalDateTime time2 = LocalDateTime.now();
        Event testEvent2 = new Event(4, 1, testJobId2, true, time2);
        this.eventDao.save(testEvent2);

        LocalDateTime firstTime = this.eventDao.getTimeOfFirstEvent();

        Assertions.assertEquals(time1.truncatedTo(ChronoUnit.SECONDS), firstTime.truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    public void testGetEventsByTime() throws FallobException, InterruptedException {
        Event testEvent1 = new Event(1, 0, this.testJobId1, true, LocalDateTime.now());
        this.eventDao.save(testEvent1);
        Thread.sleep(1000);

        Event testEvent2 = new Event(3, 2, this.testJobId2, true, LocalDateTime.now());
        this.eventDao.save(testEvent2);
        Thread.sleep(1000);

        Event testEvent3 = new Event(3, 2, this.testJobId2, false, LocalDateTime.now());
        this.eventDao.save(testEvent3);
        Thread.sleep(1000);

        LocalDateTime time = LocalDateTime.now();
        Thread.sleep(1000);

        Event testEvent4 = new Event(1, 0, this.testJobId1, false, LocalDateTime.now());
        this.eventDao.save(testEvent4);

        List<Event> activeEvents = this.eventDao.getEventsByTime(time);

        Assertions.assertEquals(1, activeEvents.size());
        Assertions.assertEquals(this.testJobId1, activeEvents.get(0).getJobID());
        Assertions.assertTrue(activeEvents.get(0).isLoad());
    }

    @AfterEach
    public void cleanup() {
        TestUtility.removeDatabaseCopy(this.databasePath);
        this.eventDao = null;
        this.connection = null;
        this.databasePath = null;
    }
}
