package edu.kit.fallob.backend.database;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.DatabaseConnectionFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.database.UserDao;
import edu.kit.fallob.dataobjects.Admin;
import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.dataobjects.NormalUser;
import edu.kit.fallob.dataobjects.SubmitType;
import edu.kit.fallob.dataobjects.User;
import edu.kit.fallob.springConfig.FallobException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.util.ArrayList;

public class UserDaoTests {
    private static final String TEST_USERNAME = "test";
    private static final User TEST_USER = new NormalUser(TEST_USERNAME, "123", "test@test.com");
    private static final String TEST_USERNAME_ADMIN = "admin";
    private static final User TEST_ADMIN = new Admin(TEST_USERNAME_ADMIN, "456", "admin@test.com");

    private String databasePath;
    private Connection connection;
    private UserDao userDao;
    private JobDao jobDao;

    @BeforeEach
    public void setup() throws FallobException {
        this.databasePath = TestUtility.createDatabaseCopy();
        this.connection = TestUtility.getConnection(this.databasePath);

        try(MockedStatic<DatabaseConnectionFactory> factoryMock = Mockito.mockStatic(DatabaseConnectionFactory.class)) {
            factoryMock.when(DatabaseConnectionFactory::getConnection).thenReturn(this.connection);

            DaoFactory daoFactory = new DaoFactory();
            this.userDao = daoFactory.getUserDao();
            this.jobDao = daoFactory.getJobDao();
        }

        //set verified to true because it's always set to true for the integration tests and otherwise the test fail
        TEST_USER.setVerified(true);
        TEST_ADMIN.setVerified(true);
    }

    @Test
    public void testSaveAndGetSingle() throws FallobException {
        this.userDao.save(TEST_USER);

        User returnedUser = this.userDao.getUserByUsername(TEST_USERNAME);

        Assertions.assertTrue(this.compareUsers(TEST_USER, returnedUser));
    }

    @Test
    public void testSaveAndGetMultiple() throws FallobException {
        this.userDao.save(TEST_USER);
        this.userDao.save(TEST_ADMIN);

        User returnedUser = this.userDao.getUserByUsername(TEST_USERNAME);
        User returnedAdmin = this.userDao.getUserByUsername(TEST_USERNAME_ADMIN);

        Assertions.assertTrue(this.compareUsers(TEST_USER, returnedUser));
        Assertions.assertTrue(this.compareUsers(TEST_ADMIN, returnedAdmin));
    }

    @Test
    public void testRemoval() throws FallobException {
        this.userDao.save(TEST_USER);
        this.userDao.remove(TEST_USERNAME);

        Assertions.assertThrows(FallobException.class, () -> {
            this.userDao.getUserByUsername(TEST_USERNAME);
        });
    }

    @Test
    public void testGetUsernameByJobId() throws FallobException {
        this.userDao.save(TEST_USER);

        //create a test job configuration
        JobConfiguration testConfiguration = new JobConfiguration("testJob", 1.0, "SAT");

        int jobId = jobDao.saveJobConfiguration(testConfiguration, TEST_USERNAME, 1);

        String returnedUsername = this.userDao.getUsernameByJobId(jobId);
        Assertions.assertEquals(TEST_USERNAME, returnedUsername);
    }

    @Test
    public void testGetUsernameByDescriptionId() throws FallobException {
        this.userDao.save(TEST_USER);

        //create a test job description object
        JobDescription jobDescription = new JobDescription(new ArrayList<>(), SubmitType.EXCLUSIVE);

        int descriptionId = this.jobDao.saveJobDescription(jobDescription, TEST_USERNAME);

        String returnedUsername = this.userDao.getUsernameByDescriptionId(descriptionId);
        Assertions.assertEquals(TEST_USERNAME, returnedUsername);
    }



    @AfterEach
    public void cleanup() {
        TestUtility.removeDatabaseCopy(this.databasePath);
        this.userDao = null;
        this.connection = null;
        this.databasePath = null;
    }

    private boolean compareUsers(User user1, User user2) {
        //check if both classes are an instance from the same subclass
        if (user1.getClass().equals(user2.getClass())) {
            if (user1.getUsername().equals(user2.getUsername()) && user1.getPassword().equals(user2.getPassword())) {
                return user1.isVerified() == user2.isVerified() && user1.getEmail().equals(user2.getEmail())
                        && user1.getPriority() == user2.getPriority();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
