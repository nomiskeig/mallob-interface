package edu.kit.fallob.backend.database;

import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.DatabaseConnectionFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.database.UserDao;
import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.dataobjects.JobInformation;
import edu.kit.fallob.dataobjects.JobResult;
import edu.kit.fallob.dataobjects.JobStatus;
import edu.kit.fallob.dataobjects.NormalUser;
import edu.kit.fallob.dataobjects.ResultMetaData;
import edu.kit.fallob.dataobjects.SubmitType;
import edu.kit.fallob.dataobjects.User;
import edu.kit.fallob.springConfig.FallobException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobDaoTests {
    private static final String TEST_USERNAME = "test";

    private FallobConfiguration config = Mockito.spy(FallobConfiguration.getInstance());

    private String testFilePath;
    private String descriptionDir;
    private String resultDir;
    private String databasePath;
    private Connection connection;
    private UserDao userDao;
    private JobDao jobDao;

    @BeforeEach
    public void setup() throws FallobException {
        this.databasePath = TestUtility.createDatabaseCopy();
        this.descriptionDir = TestUtility.createNewDescriptionDir();
        this.testFilePath = TestUtility.createTestFileCopy();
        this.resultDir = TestUtility.createNewResultDir();
        this.connection = TestUtility.getConnection(this.databasePath);

        try(MockedStatic<DatabaseConnectionFactory> factoryMock = Mockito.mockStatic(DatabaseConnectionFactory.class)) {
            factoryMock.when(DatabaseConnectionFactory::getConnection).thenReturn(this.connection);

            Mockito.when(this.config.getDescriptionsbasePath()).thenReturn(this.descriptionDir);
            Mockito.when(this.config.getResultBasePath()).thenReturn(this.resultDir);
            try (MockedStatic<FallobConfiguration> mockedConfig = Mockito.mockStatic(FallobConfiguration.class)) {
                mockedConfig.when(FallobConfiguration::getInstance).thenReturn(config);

                DaoFactory daoFactory = new DaoFactory();
                this.userDao = daoFactory.getUserDao();
                this.jobDao = daoFactory.getJobDao();
            }
        }
        User testUser = new NormalUser(TEST_USERNAME, "123", "test@test.com");
        this.userDao.save(testUser);
    }

    @Test
    public void testSaveAndGetJobDescription() throws FallobException, IOException {
        List<File> descriptionFiles = new ArrayList<>();
        File descriptionFile = new File(this.testFilePath);
        Assertions.assertTrue(descriptionFile.isFile());
        descriptionFiles.add(descriptionFile);

        List<Long> expectedFileLengths = new ArrayList<>();
        long expectedLength = descriptionFile.length();
        expectedFileLengths.add(expectedLength);

        JobDescription testDescription = new JobDescription(descriptionFiles, SubmitType.EXCLUSIVE);

        int descriptionId = this.jobDao.saveJobDescription(testDescription, TEST_USERNAME);
        Assertions.assertTrue(descriptionId > 0);

        JobDescription returnedJobDescription = this.jobDao.getJobDescription(descriptionId);
        this.compareJobDescriptions(returnedJobDescription, expectedFileLengths, SubmitType.EXCLUSIVE);
    }

    @Test
    public void testRemoveOldestJobDescription() throws FallobException, InterruptedException, IOException {
        List<File> descriptionFiles = new ArrayList<>();
        File descriptionFile = new File(this.testFilePath);
        Assertions.assertTrue(descriptionFile.isFile());
        descriptionFiles.add(descriptionFile);

        List<Long> expectedFileLengths = new ArrayList<>();
        long expectedLength = descriptionFile.length();
        expectedFileLengths.add(expectedLength);

        JobDescription testDescription1 = new JobDescription(new ArrayList<>(), SubmitType.EXCLUSIVE);
        JobDescription testDescription2 = new JobDescription(descriptionFiles, SubmitType.URL);

        int descriptionId1 = this.jobDao.saveJobDescription(testDescription1, TEST_USERNAME);
        Thread.sleep(1000);
        int descriptionId2 = this.jobDao.saveJobDescription(testDescription2, TEST_USERNAME);

        this.jobDao.removeOldestJobDescription();

        JobDescription returnedDescription = this.jobDao.getJobDescription(descriptionId2);

        this.compareJobDescriptions(returnedDescription, expectedFileLengths, SubmitType.URL);

        Assertions.assertThrows(FallobException.class, () -> this.jobDao.getJobDescription(descriptionId1));
    }

    @Test
    public void testSaveAndGetJobConfiguration() throws FallobException {
        JobDescription testDescription = new JobDescription(new ArrayList<>(), SubmitType.URL);
        int descriptionId = this.jobDao.saveJobDescription(testDescription, TEST_USERNAME);

        JobConfiguration testConfig = new JobConfiguration("test", 1.0, "SAT");
        testConfig.setDescriptionID(descriptionId);

        int jobId = this.jobDao.saveJobConfiguration(testConfig, TEST_USERNAME, 1);

        JobConfiguration returnedConfig = this.jobDao.getJobConfiguration(jobId);
        this.compareJobConfigurations(testConfig, returnedConfig);
    }

    @Test
    public void testRemoveJobConfiguration() throws FallobException, InterruptedException {
        JobDescription testDescription1 = new JobDescription(new ArrayList<>(), SubmitType.URL);
        int descriptionId1 = this.jobDao.saveJobDescription(testDescription1, TEST_USERNAME);
        JobDescription testDescription2 = new JobDescription(new ArrayList<>(), SubmitType.EXCLUSIVE);
        int descriptionId2 = this.jobDao.saveJobDescription(testDescription2, TEST_USERNAME);

        JobConfiguration config1 = new JobConfiguration("test1", 1.0, "SAT");
        config1.setDescriptionID(descriptionId1);
        JobConfiguration config2 = new JobConfiguration("test2", 0.5, "SAT");
        config2.setDescriptionID(descriptionId2);

        int jobId1 = this.jobDao.saveJobConfiguration(config1, TEST_USERNAME, 1);
        Thread.sleep(1000);
        LocalDateTime time = LocalDateTime.now();
        Thread.sleep(1000);
        int jobId2 = this.jobDao.saveJobConfiguration(config2, TEST_USERNAME, 2);

        this.jobDao.removeAllJobsBeforeTime(time);

        Assertions.assertThrows(FallobException.class, () -> {
            this.jobDao.getJobConfiguration(jobId1);
        });

        JobConfiguration returnedConfig2 = this.jobDao.getJobConfiguration(jobId2);
        this.compareJobConfigurations(config2, returnedConfig2);
    }

    @Test
    public void testGetAllJobIds() throws FallobException {
        JobDescription testDescription1 = new JobDescription(new ArrayList<>(), SubmitType.URL);
        int descriptionId1 = this.jobDao.saveJobDescription(testDescription1, TEST_USERNAME);
        JobDescription testDescription2 = new JobDescription(new ArrayList<>(), SubmitType.EXCLUSIVE);
        int descriptionId2 = this.jobDao.saveJobDescription(testDescription2, TEST_USERNAME);

        JobConfiguration config1 = new JobConfiguration("test1", 1.0, "SAT");
        config1.setDescriptionID(descriptionId1);
        JobConfiguration config2 = new JobConfiguration("test2", 0.5, "SAT");
        config2.setDescriptionID(descriptionId2);

        int jobId1 = this.jobDao.saveJobConfiguration(config1, TEST_USERNAME, 1);
        int jobId2 = this.jobDao.saveJobConfiguration(config2, TEST_USERNAME, 2);

        int[] jobIds = this.jobDao.getAllJobIds(TEST_USERNAME);

        Assertions.assertEquals(jobIds.length, 2);
        Assertions.assertEquals(jobIds[0], jobId1);
        Assertions.assertEquals(jobIds[1], jobId2);
    }

    @Test
    public void testGetJobStatus() throws FallobException {
        JobDescription testDescription = new JobDescription(new ArrayList<>(), SubmitType.URL);
        int descriptionId = this.jobDao.saveJobDescription(testDescription, TEST_USERNAME);
        JobConfiguration config = new JobConfiguration("test1", 1.0, "SAT");
        config.setDescriptionID(descriptionId);
        int jobId = this.jobDao.saveJobConfiguration(config, TEST_USERNAME, 1);

        JobStatus status = this.jobDao.getJobStatus(jobId);

        Assertions.assertEquals(status, JobStatus.RUNNING);
    }

    @Test
    public void testGetJobInformation() throws FallobException {
        JobDescription testDescription = new JobDescription(new ArrayList<>(), SubmitType.URL);
        int descriptionId = this.jobDao.saveJobDescription(testDescription, TEST_USERNAME);
        JobConfiguration config = new JobConfiguration("test1", 1.0, "SAT");
        config.setDescriptionID(descriptionId);
        int jobId = this.jobDao.saveJobConfiguration(config, TEST_USERNAME, 1);

        JobInformation information;
        //mock the database because the method needs the database internally
        try(MockedStatic<DatabaseConnectionFactory> factoryMock = Mockito.mockStatic(DatabaseConnectionFactory.class)) {
            factoryMock.when(DatabaseConnectionFactory::getConnection).thenReturn(this.connection);
            information = this.jobDao.getJobInformation(jobId);
        }

        Assertions.assertEquals(information.getJobID(), jobId);
        Assertions.assertEquals(information.getJobStatus(), JobStatus.RUNNING);
        Assertions.assertEquals(information.getUser().getUsername(), TEST_USERNAME);
        Assertions.assertNull(information.getResultMetaData());
        this.compareJobConfigurations(information.getJobConfiguration(), config);
    }

    @Test
    public void testAddResultAndGetResult() throws FallobException {
        JobDescription testDescription = new JobDescription(new ArrayList<>(), SubmitType.URL);
        long expectedLength = new File(this.testFilePath).length();
        int descriptionId = this.jobDao.saveJobDescription(testDescription, TEST_USERNAME);
        JobConfiguration config = new JobConfiguration("test1", 1.0, "SAT");
        config.setDescriptionID(descriptionId);
        int jobId = this.jobDao.saveJobConfiguration(config, TEST_USERNAME, 1);

        JobResult testResult = new JobResult(new File(this.testFilePath));
        ResultMetaData testMetaData = new ResultMetaData(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);

        this.jobDao.addJobResult(jobId, testResult, testMetaData);

        JobResult returnedResult = this.jobDao.getJobResult(jobId);

        Assertions.assertEquals(expectedLength, returnedResult.getResult().length());
    }

    @Test
    public void testUpdateJobStatus() throws FallobException {
        JobDescription testDescription = new JobDescription(new ArrayList<>(), SubmitType.URL);
        int descriptionId = this.jobDao.saveJobDescription(testDescription, TEST_USERNAME);
        JobConfiguration config = new JobConfiguration("test1", 1.0, "SAT");
        config.setDescriptionID(descriptionId);
        int jobId = this.jobDao.saveJobConfiguration(config, TEST_USERNAME, 1);

        JobStatus statusBeforeUpdate = this.jobDao.getJobStatus(jobId);
        Assertions.assertEquals(JobStatus.RUNNING, statusBeforeUpdate);

        this.jobDao.updateJobStatus(jobId, JobStatus.CANCELLED);

        JobStatus statusAfterUpdate = this.jobDao.getJobStatus(jobId);
        Assertions.assertEquals(JobStatus.CANCELLED, statusAfterUpdate);
    }

    @Test
    public void testGetJobIdByMallobId() throws FallobException {
        int mallobId = 1;
        JobDescription testDescription = new JobDescription(new ArrayList<>(), SubmitType.URL);
        int descriptionId = this.jobDao.saveJobDescription(testDescription, TEST_USERNAME);
        JobConfiguration config = new JobConfiguration("test1", 1.0, "SAT");
        config.setDescriptionID(descriptionId);
        int jobId = this.jobDao.saveJobConfiguration(config, TEST_USERNAME, mallobId);

        int returnedJobId = this.jobDao.getJobIdByMallobId(mallobId);

        Assertions.assertEquals(jobId, returnedJobId);
    }

    @Test
    public void testGetMallobIdByJobId() throws FallobException {
        int mallobId = 1;
        JobDescription testDescription = new JobDescription(new ArrayList<>(), SubmitType.URL);
        int descriptionId = this.jobDao.saveJobDescription(testDescription, TEST_USERNAME);
        JobConfiguration config = new JobConfiguration("test1", 1.0, "SAT");
        config.setDescriptionID(descriptionId);
        int jobId = this.jobDao.saveJobConfiguration(config, TEST_USERNAME, mallobId);

        int returnedMallobId = this.jobDao.getMallobIdByJobId(jobId);

        Assertions.assertEquals(mallobId, returnedMallobId);
    }

    @Test
    public void testGetSizeOfJobDescriptions() throws FallobException {
        List<File> descriptionFiles = new ArrayList<>();
        File descriptionFile = new File(this.testFilePath);
        descriptionFiles.add(descriptionFile);
        JobDescription testDescription = new JobDescription(descriptionFiles, SubmitType.URL);
        int descriptionId = this.jobDao.saveJobDescription(testDescription, TEST_USERNAME);

        long sizeOfConfigs = this.jobDao.getSizeOfAllJobDescriptions();

        Assertions.assertEquals(1, sizeOfConfigs);
    }

    @Test
    public void testGetAllRunningJobs() throws FallobException {
        JobDescription testDescription1 = new JobDescription(new ArrayList<>(), SubmitType.URL);
        int descriptionId1 = this.jobDao.saveJobDescription(testDescription1, TEST_USERNAME);
        JobDescription testDescription2 = new JobDescription(new ArrayList<>(), SubmitType.EXCLUSIVE);
        int descriptionId2 = this.jobDao.saveJobDescription(testDescription2, TEST_USERNAME);

        JobConfiguration config1 = new JobConfiguration("test1", 1.0, "SAT");
        config1.setDescriptionID(descriptionId1);
        JobConfiguration config2 = new JobConfiguration("test2", 0.5, "SAT");
        config2.setDescriptionID(descriptionId2);

        int jobId1 = this.jobDao.saveJobConfiguration(config1, TEST_USERNAME, 1);
        int jobId2 = this.jobDao.saveJobConfiguration(config2, TEST_USERNAME, 2);

        List<Integer> runningBeforeUpdate = this.jobDao.getAllRunningJobs();
        Assertions.assertEquals(2, runningBeforeUpdate.size());

        this.jobDao.updateJobStatus(jobId1, JobStatus.DONE);
        List<Integer> runningAfterUpdate = this.jobDao.getAllRunningJobs();

        Assertions.assertEquals(1, runningAfterUpdate.size());
        Assertions.assertEquals(jobId2, runningAfterUpdate.get(0));
    }

    @AfterEach
    public void cleanup() throws IOException {
        TestUtility.removeDatabaseCopy(this.databasePath);
        TestUtility.deleteDirs();
        TestUtility.removeTestFileCopy(this.testFilePath);
        this.userDao = null;
        this.jobDao = null;
        this.connection = null;
        this.databasePath = null;
        this.descriptionDir = null;
        this.resultDir = null;
        this.testFilePath = null;
    }

    private void compareJobDescriptions(JobDescription jobDescription, List<Long> expectedFileSizes, SubmitType expectedSubmitType) throws IOException {
       Assertions.assertEquals(jobDescription.getSubmitType(), expectedSubmitType);

        Assertions.assertEquals(jobDescription.getDescriptionFiles().size(), expectedFileSizes.size());
        List<File> descriptionFiles = jobDescription.getDescriptionFiles();

        for (int i = 0; i < jobDescription.getDescriptionFiles().size(); i++) {
            File description = descriptionFiles.get(i);
            long expectedFileSize = expectedFileSizes.get(i);

            Assertions.assertEquals(description.length(), expectedFileSize);
        }
    }

    private void compareJobConfigurations(JobConfiguration config1, JobConfiguration config2) {
        Assertions.assertEquals(config1.getName(), config2.getName());
        Assertions.assertEquals(config1.getPriority(), config2.getPriority());
        Assertions.assertEquals(config1.getApplication(), config2.getApplication());
        Assertions.assertEquals(config1.getDescriptionID(), config2.getDescriptionID());
    }
}
