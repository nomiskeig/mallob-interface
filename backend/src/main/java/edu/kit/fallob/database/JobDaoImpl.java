package edu.kit.fallob.database;

import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.dataobjects.JobInformation;
import edu.kit.fallob.dataobjects.JobResult;
import edu.kit.fallob.dataobjects.JobStatus;
import edu.kit.fallob.dataobjects.ResultMetaData;
import edu.kit.fallob.dataobjects.SubmitType;
import edu.kit.fallob.dataobjects.User;
import edu.kit.fallob.springConfig.FallobException;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * class that is responsible for storing all relevant data for the jobs in the database and the filesystem
 * @author Valentin Schenk
 * @version 1.0
 */
public class JobDaoImpl implements JobDao{

    private final Connection conn;
    private final FallobConfiguration configuration;

    //constants for different regex and strings that are required for handling file paths
    private static final String SINGLE_FILE_REGEX = "%o\\..*";
    private static final String DESCRIPTION_FILES_REGEX = "%s\\d+\\..*";
    private static final String FILE_EXTENSION_REGEX = "\\.";
    private static final String DIRECTORY_SEPARATOR = "/";
    private static final String FILE_SEPARATOR = ".";
    private static final String NAME_SEPARATOR = "_";
    private static final String ARRAY_TYPE_INT = "INT";
    private static final String ARRAY_TYPE_STRING = "VARCHAR(255)";

    //error messages that get returned if an error occurs in the databas
    private static final String DATABASE_ERROR = "An error occurred while accessing the database";
    private static final String DATABASE_NOT_FOUND = "Error, the requested entry couldn't be found";

    //the sql queries that are required for the database interaction
    private static final String JOB_INSERT = "INSERT INTO job (username, submissionTime, jobStatus, mallobId) VALUES (?, ?, ?, ?)";
    private static final String CONFIGURATION_INSERT = "INSERT INTO jobConfiguration (jobId, name, priority, application, maxDemand, wallclockLimit, cpuLimit, arrival, dependencies, incremental, precursor, contentMode, additionalConfig, dependenciesStrings, precursorString) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_JOB_ID = "UPDATE jobDescription SET jobId=? WHERE descriptionId=?";
    private static final String DESCRIPTION_INSERT = "INSERT INTO jobDescription (username, submitType, uploadTime) VALUES (?, ?, ?)";
    private static final String GET_OLDEST_JOB_DESCRIPTION = "SELECT descriptionId FROM jobDescription WHERE MIN(uploadTime)";
    private static final String GET_JOBS_BEFORE_TIME = "SELECT jobId FROM job WHERE submissionTime < ?";
    private static final String DELETE_FROM_JOB = "DELETE FROM job WHERE jobID=?";
    private static final String DELETE_FROM_JOB_CONFIGURATION = "DELETE FROM jobConfiguration WHERE jobId=?";
    private static final String DELETE_FROM_META_DATA = "DELETE FROM resultMetaData WHERE jobId=?";
    private static final String GET_ALL_JOBIDS = "SELECT jobId FROM job WHERE username=?";
    private static final String GET_SUBMIT_TYPE = "SELECT submitType FROM jobDescription WHERE descriptionId=?";
    private static final String GET_JOB_CONFIGURATION = "SELECT * FROM jobConfiguration WHERE jobId=?";
    private static final String GET_DESCRIPTION_ID = "SELECT descriptionId FROM jobDescription WHERE jobId=?";
    private static final String GET_JOB_STATUS = "SELECT jobStatus FROM job WHERE jobId=?";
    private static final String GET_RESULT_META_DATA = "SELECT * FROM resultMetaData WHERE jobId=?";
    private static final String GET_JOB_INFORMATION = "SELECT username, submissionTime FROM job WHERE jobId=?";
    private static final String UPDATE_JOB_STATUS = "UPDATE job SET jobStatus=? WHERE jobId=?";
    private static final String INSERT_RESULT_META_DATA = "INSERT INTO resultMetaData (jobId, usedWallclockSeconds, usedCpuSeconds, timeParsing, timeProcessing, timeScheduling, timeTotal) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String JOB_ID_BY_MALLOB_ID = "SELECT jobId FROM job WHERE mallobId=?";
    private static final String MALLOB_ID_BY_JOB_ID = "SELECT mallobId FROM job WHERE jobId=?";
    private static final String GET_JOBS_WITH_STATUS = "SELECT jobId FROM job WHERE jobStatus=?";

    /**
     * the constructor of the class
     * @throws FallobException if the connection to the database couldn't be established
     */
    public JobDaoImpl() throws FallobException {
        this.conn = DatabaseConnectionFactory.getConnection();
        this.configuration = FallobConfiguration.getInstance();
    }

    /**
     * saves a job-configuration with some additional data persistently in the database
     * @param configuration the job-configuration that should be saved
     * @param username the name of the user that uploaded the-job configuration
     * @param mallobId the id of the job which was assigned to it by mallob
     * @return the jobId which was generated by the database
     * @throws FallobException if an error occurs while accessing the database
     */
    @Override
    public int saveJobConfiguration(JobConfiguration configuration, String username, int mallobId) throws FallobException {
        try {
            //add data to job table
            PreparedStatement jobStatement = conn.prepareStatement(JOB_INSERT);
            jobStatement.setString(1, username);
            //set the current time as submission time
            jobStatement.setObject(2, LocalDateTime.now());
            //set the current job status to running
            jobStatement.setString(3, JobStatus.RUNNING.name());
            jobStatement.setInt(4, mallobId);

            jobStatement.executeUpdate();

            int jobId = this.getGeneratedKey(jobStatement);

            //insert the configuration into the jobConfiguration table
            //this method is separate because otherwise the method would be too long
            this.saveInJobConfiguration(configuration, jobId);

            //update the entry in the jobDescription table to set the jobId
            PreparedStatement updateStatement = this.conn.prepareStatement(UPDATE_JOB_ID);
            updateStatement.setInt(1, jobId);
            updateStatement.setInt(2, configuration.getDescriptionID());

            updateStatement.executeUpdate();

            return jobId;
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }

    /**
     * saves a job-description persistently
     * @param description the job-description that should be saved
     * @param username the name of the user who uploaded the job-description
     * @return the descriptionId which was assigned by the database
     * @throws FallobException if an error occurs while accessing the database
     */
    @Override
    public int saveJobDescription(JobDescription description, String username) throws FallobException {
        try {
            //add the entry to the database
            PreparedStatement statement = this.conn.prepareStatement(DESCRIPTION_INSERT);
            statement.setString(1, username);
            statement.setString(2, description.getSubmitType().name());
            statement.setObject(3, LocalDateTime.now());

            statement.executeUpdate();

            int descriptionId = this.getGeneratedKey(statement);

            //save description on the filesystem
            List<File> files = description.getDescriptionFiles();

            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                String fileExtension = this.getFileExtension(file);

                String newPath = configuration.getDescriptionsbasePath() + DIRECTORY_SEPARATOR + descriptionId + NAME_SEPARATOR + i + FILE_SEPARATOR + fileExtension;
                FileHandler.saveFileAtPath(file, newPath);
            }
            return descriptionId;
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }

    /**
     * removes the job-description from the storage that was stored the longest out of all job-descriptions
     * @throws FallobException if an error occurs while accessing the database
     */
    @Override
    public void removeOldestJobDescription() throws FallobException {
        int descriptionId = 0;

        try {
            //get descriptionId of the oldest job description
            PreparedStatement getDescriptionId = this.conn.prepareStatement(GET_OLDEST_JOB_DESCRIPTION);
            ResultSet result = getDescriptionId.executeQuery();

            if(result.next()) {
                descriptionId = result.getInt(1);
            }

            PreparedStatement statement = this.conn.prepareStatement(DELETE_FROM_JOB_CONFIGURATION);
            statement.setInt(1, descriptionId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }

        //remove the descriptionFiles from the filesystem
        String path = this.configuration.getDescriptionsbasePath();
        String regex = String.format(DESCRIPTION_FILES_REGEX, descriptionId);

        FileHandler.deleteFilesByRegex(path, regex);
    }

    /**
     * removes all job related data from the database for the jobs which were submitted before the given time
     * @param time the time from where on the job data is removed from the database
     * @throws FallobException if an error occurs while accessing the database
     */
    @Override
    public void removeAllJobsBeforeTime(LocalDateTime time) throws FallobException {
        try {
            PreparedStatement getStatement = this.conn.prepareStatement(GET_JOBS_BEFORE_TIME);
            getStatement.setString(1, time.toString());

            ResultSet result = getStatement.executeQuery();

            //delete every job from the database
            while (result.next()) {
                int jobId = result.getInt(1);
                //set jobId value in jobDescription table entries to null to prevent errors from the database
                PreparedStatement updateStatement = this.conn.prepareStatement(UPDATE_JOB_ID);
                updateStatement.setNull(1, Types.INTEGER);
                updateStatement.setInt(2, jobId);
                updateStatement.executeUpdate();

                //delete the entry from the jobConfiguration table
                PreparedStatement deleteConfig = this.conn.prepareStatement(DELETE_FROM_JOB_CONFIGURATION);
                deleteConfig.setInt(1, jobId);
                deleteConfig.executeUpdate();

                //delete entry from the resultMetaData table
                PreparedStatement deleteMetaData = this.conn.prepareStatement(DELETE_FROM_META_DATA);
                deleteMetaData.setInt(1, jobId);
                deleteConfig.executeUpdate();

                //delete entry from job table
                PreparedStatement deleteJob = this.conn.prepareStatement(DELETE_FROM_JOB);
                deleteJob.setInt(1, jobId);
                deleteJob.executeUpdate();

                //delete the result file from the filesystem
                String resultDirectoryPath = this.configuration.getResultBasePath();
                String regex = String.format(SINGLE_FILE_REGEX, jobId);
                FileHandler.deleteFilesByRegex(resultDirectoryPath, regex);
            }
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }

    /**
     * gives the ids if all the stored jobs for a user
     * @param username the name of the user whose job-ids should be returned
     * @return an array that contains all the job-ids
     * @throws FallobException if an error occurs while accessing the database
     */
    @Override
    public int[] getAllJobIds(String username) throws FallobException {
        try {
            List<Integer> jobIds = new ArrayList<>();

            PreparedStatement statement = this.conn.prepareStatement(GET_ALL_JOBIDS);
            statement.setString(1, username);

            ResultSet result = statement.executeQuery();

            //iterate over all jobIds
            while (result.next()) {
                jobIds.add(result.getInt(1));
            }

            return jobIds.stream().mapToInt(i -> i).toArray();
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }

    /**
     * getter for a specific job-description
     * @param descriptionId the id of the job-description that should be returned
     * @return the job-description
     * @throws FallobException if an error occurs while accessing the database or if the description couldn't be found
     */
    @Override
    public JobDescription getJobDescription(int descriptionId) throws FallobException {
        //get the description files from the filesystem
        String filesPath = this.configuration.getDescriptionsbasePath();
        String regex = String.format(DESCRIPTION_FILES_REGEX, descriptionId + NAME_SEPARATOR);
        List<File> descriptionFiles = FileHandler.getFilesByRegex(filesPath, regex);

        //get the submit-type from the database
        try {
            PreparedStatement statement = this.conn.prepareStatement(GET_SUBMIT_TYPE);
            statement.setInt(1, descriptionId);

            ResultSet result = statement.executeQuery();

            if(result.next()) {
                String submitTypeString = result.getString(1);
                //convert string back to enum value
                SubmitType submitType = SubmitType.valueOf(submitTypeString);

                return new JobDescription(descriptionFiles, submitType);
            } else {
                throw new FallobException(HttpStatus.NOT_FOUND, DATABASE_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }

    /**
     * getter for a specific job-configuration
     * @param jobId the id of the job whose configuration should be returned
     * @return the job-configuration
     * @throws FallobException if an error occurs while accessing the database or if the configuration couldn't be found
     */
    @Override
    public JobConfiguration getJobConfiguration(int jobId) throws FallobException{
        int descriptionId = 0;
        try {
            PreparedStatement getDescriptionId = this.conn.prepareStatement(GET_DESCRIPTION_ID);
            getDescriptionId.setInt(1, jobId);

            ResultSet descriptionIdResult = getDescriptionId.executeQuery();
            if (descriptionIdResult.next()) {
                descriptionId = descriptionIdResult.getInt(1);
            } else {
                throw new FallobException(HttpStatus.NOT_FOUND, DATABASE_NOT_FOUND);
            }

            PreparedStatement getConfiguration = this.conn.prepareStatement(GET_JOB_CONFIGURATION);
            getConfiguration.setInt(1, jobId);

            ResultSet result = getConfiguration.executeQuery();
            //get the number of columns of the result
            ResultSetMetaData metaData = result.getMetaData();
            int columnCount = metaData.getColumnCount();

            if (result.next()) {
                //starting by 2 because the first column is the jobId
                String name = result.getString(2);
                String application = result.getString(3);
                float priority = result.getFloat(4);
                int maxDemand = result.getInt(5);
                String wallclockLimit = result.getString(6);
                String cpuLimit = result.getString(7);
                String arrival = result.getString(8);
                Integer[] dependencies = (Integer[]) result.getArray(9).getArray();
                boolean incremental = result.getBoolean(10);
                String precursor = result.getString(11);
                String contentMode = result.getString(12);
                String additionalConfig = result.getString(13);
                String[] dependenciesString = (String[]) result.getArray(14).getArray();
                String precursorString = result.getString(15);


                //TODO: remove interrupt, literals, assumptions and done from jobConfiguration class and make constructor with dependenciesString and precursorString parameter
                //return new JobConfiguration(name, priority, application, maxDemand, wallclockLimit, cpuLimit, arrival, dependencies, incremental, precursor, descriptionId, additionalConfig, contentMode);
                return null;
            } else {
                throw new FallobException(HttpStatus.NOT_FOUND, DATABASE_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }

    /**
     * returns the status of a specific job
     * @param jobId the id of the job whose status should be returned
     * @return the job-status
     * @throws FallobException if an error occurs while accessing the database or if the job couldn't be found
     */
    @Override
    public JobStatus getJobStatus(int jobId) throws FallobException {
        try {
            PreparedStatement statement = this.conn.prepareStatement(GET_JOB_STATUS);
            statement.setInt(1, jobId);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String statusString = result.getString(1);
                return JobStatus.valueOf(statusString);
            } else {
                throw new FallobException(HttpStatus.NOT_FOUND, DATABASE_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }

    /**
     * returns information about a specific job
     * @param jobId the id if the job for which the information should be returned
     * @return the information of the job
     * @throws FallobException if an error occurs while accessing the database or if the job couldn't be found
     */
    @Override
    public JobInformation getJobInformation(int jobId) throws FallobException {
        JobConfiguration configuration = this.getJobConfiguration(jobId);
        ResultMetaData metaData = this.getResultMetaData(jobId);
        JobStatus status = this.getJobStatus(jobId);

        try {
            PreparedStatement statement = this.conn.prepareStatement(GET_JOB_INFORMATION);
            statement.setInt(1, jobId);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String username = result.getString(1);
                LocalDateTime submissionTime = result.getTimestamp(2).toLocalDateTime();

                //get a UserDao object to get the user object
                DaoFactory daoFactory = new DaoFactory();
                UserDao userDao = daoFactory.getUserDao();

                User user = userDao.getUserByUsername(username);

                //TODO: change submissionTime to LocalDateTime
                return new JobInformation(configuration, metaData, user, submissionTime.toString(), status, jobId);
            } else {
                throw new FallobException(HttpStatus.NOT_FOUND, DATABASE_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }

    }

    /**
     * returns the result of a specific job
     * @param jobId the id of the job whose result should be returned
     * @return the result of the job
     * @throws FallobException if the result file couldn't be found
     */
    @Override
    public JobResult getJobResult(int jobId) throws FallobException{
        String path = this.configuration.getResultBasePath();
        String regex = String.format(SINGLE_FILE_REGEX, jobId);
        List<File> resultFile = FileHandler.getFilesByRegex(path, regex);

        if (resultFile.size() == 1) {
            return new JobResult(resultFile.get(0));
        } else {
            throw new FallobException(HttpStatus.NOT_FOUND, DATABASE_NOT_FOUND);
        }
    }

    /**
     * updates the status of a job
     * @param jobId the id of the job whose status should be updated
     * @param status the new status of the job
     * @throws FallobException if an error occurs while accessing the database
     */
    @Override
    public void updateJobStatus(int jobId, JobStatus status) throws FallobException {
        try {
            PreparedStatement statement = this.conn.prepareStatement(UPDATE_JOB_STATUS);
            statement.setString(1, status.name());
            statement.setInt(2, jobId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }

    /**
     * adds a result for a specif job
     * @param jobId the id of the job to which the result should be added
     * @param result the result object of the job
     * @param resultMetaData the object that contains the meta data of the result
     * @throws FallobException if an error occurs while accessing the database
     */
    @Override
    public void addJobResult(int jobId, JobResult result, ResultMetaData resultMetaData) throws FallobException {
        //store result file on the filesystem
        File resultFile = result.getResult();
        String fileExtension = this.getFileExtension(resultFile);
        String newPath = this.configuration.getResultBasePath() + DIRECTORY_SEPARATOR + jobId + FILE_SEPARATOR + fileExtension;

        FileHandler.saveFileAtPath(resultFile, newPath);

        //save result meta data in the database
        try {
            PreparedStatement statement = this.conn.prepareStatement(INSERT_RESULT_META_DATA);
            statement.setInt(1, jobId);
            statement.setDouble(2, resultMetaData.getWallclockSeconds());
            statement.setDouble(3, resultMetaData.getCpuSeconds());
            statement.setDouble(4, resultMetaData.getParsingTime());
            statement.setDouble(5, resultMetaData.getProcessingTime());
            statement.setDouble(6, resultMetaData.getSchedulingTime());
            statement.setDouble(7, resultMetaData.getTotalTime());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }

    /**
     * returns the id of a job from a given mallobId
     * @param mallobId the mallob Id for which the jobId should be returned
     * @return the jobId
     * @throws FallobException if an error occurs while accessing the database or if the job couldn't be found
     */
    @Override
    public int getJobIdByMallobId(int mallobId) throws FallobException {
        try {
            PreparedStatement statement = this.conn.prepareStatement(JOB_ID_BY_MALLOB_ID);
            statement.setInt(1, mallobId);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getInt(1);
            } else {
                throw new FallobException(HttpStatus.NOT_FOUND, DATABASE_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }

    /**
     * returns the mallobId of a job from a given jobId
     * @param jobId the id of the job for which the mallobId should be returned
     * @return the mallobId
     * @throws FallobException if an error occurs while accessing the database or if the job couldn't be found
     */
    @Override
    public int getMallobIdByJobId(int jobId) throws FallobException {
        try {
            PreparedStatement statement = this.conn.prepareStatement(MALLOB_ID_BY_JOB_ID);
            statement.setInt(1, jobId);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getInt(1);
            } else {
                throw new FallobException(HttpStatus.NOT_FOUND, DATABASE_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }

    /**
     * gives the size of all the job-description files
     * @return the size off all the files in megabytes
     */
    @Override
    public long getSizeOfAllJobDescriptions() {
        String path = this.configuration.getDescriptionsbasePath();
        return FileHandler.getSizeOfDirectory(path);
    }

    /**
     * returns a list with the ids of all the jobs that are running at the current time
     * @return a list that contains all the ids of the jobs
     * @throws FallobException if an error occurs while accessing the database
     */
    @Override
    public List<Integer> getAllRunningJobs() throws FallobException {
        List<Integer> runningJobs = new ArrayList<>();

        try {
            PreparedStatement statement = this.conn.prepareStatement(GET_JOBS_WITH_STATUS);
            //get all jobs with status RUNNING
            statement.setString(1, JobStatus.RUNNING.name());

            ResultSet result = statement.executeQuery();

            //iterate over all results
            while (result.next()) {
                int jobId = result.getInt(1);
                runningJobs.add(jobId);
            }

            return runningJobs;
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }


    /**
     * gets the keys that got generated automatically by the database
     * @param statement the sql statement from which the keys should be extracted
     * @return the key that got generated by the database
     * @throws FallobException if an error occurs while accessing the database or if the result set is empty
     */
    private int getGeneratedKey(PreparedStatement statement) throws FallobException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getInt(1);
            } else {
                throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
            }
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }

    /**
     * returns the file extension of the given file
     * @param file the file for which the extension should be determined
     * @return the file extension of the file
     */
    private String getFileExtension(File file) {
        String[] fileNameSplit = file.getName().split(FILE_EXTENSION_REGEX);
        //get the index of the last array element
        int filExtensionIndex = fileNameSplit.length - 1;

        return fileNameSplit[filExtensionIndex];
    }

    /**
     * inserts a job-configuration into the jobConfiguration table
     * this method got added because the saveJobConfiguration method got too long
     * @param configuration the job-configuration that should be saved
     * @param jobId the id of the job whose configuration should be added
     * @throws FallobException if an error occurs while accessing the database
     */
    private void saveInJobConfiguration(JobConfiguration configuration, int jobId) throws FallobException {
        //add data to jobConfiguration table
        try {
            PreparedStatement configStatement = this.conn.prepareStatement(CONFIGURATION_INSERT);

            configStatement.setInt(1, jobId);
            configStatement.setString(2, configuration.getName());
            configStatement.setDouble(3, configuration.getPriority());
            configStatement.setString(4, configuration.getApplication());
            configStatement.setInt(5, configuration.getMaxDemand());
            configStatement.setString(6, configuration.getWallClockLimit());
            configStatement.setString(7, configuration.getCpuLimit());
            configStatement.setString(8, configuration.getArrival());
            //convert the dependencies int array into an Array object
            Array dependencies = this.conn.createArrayOf(ARRAY_TYPE_INT, new int[][]{configuration.getDependencies()});
            configStatement.setArray(9, dependencies);
            configStatement.setBoolean(10, configuration.isIncremental());
            configStatement.setInt(11, configuration.getPrecursor());
            configStatement.setString(12, configuration.getContentMode());
            configStatement.setString(13, configuration.getAdditionalParameter());
            //convert the dependencies string array into an Array object
            Array dependenciesStrings = this.conn.createArrayOf(ARRAY_TYPE_STRING, configuration.getDependenciesStrings());
            configStatement.setArray(14, dependenciesStrings);
            configStatement.setString(15, configuration.getPrecursorString());

            configStatement.executeUpdate();
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }

    /**
     * gets the meta data of the result of a specific job
     * @param jobId the id of the job for which the result meta data should be returned
     * @return the result meta data
     * @throws FallobException if an error occurs while accessing the database or if the job couldn't be found
     */
    private ResultMetaData getResultMetaData(int jobId) throws FallobException {
        try {
            PreparedStatement statement = this.conn.prepareStatement(GET_RESULT_META_DATA);
            statement.setInt(1, jobId);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                //start with 2 because the first column is the jobId
                double usedWallclockSeconds = result.getFloat(2);
                double usedCpuSeconds = result.getFloat(3);
                double timeParsing = result.getFloat(4);
                double timeProcessing = result.getFloat(5);
                double timeScheduling = result.getFloat(6);
                double timeTotal = result.getFloat(7);

                return new ResultMetaData(timeParsing, timeProcessing, timeScheduling, timeTotal, usedCpuSeconds, usedWallclockSeconds);
            } else {
                throw new FallobException(HttpStatus.NOT_FOUND, DATABASE_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.NOT_IMPLEMENTED, DATABASE_ERROR);
        }
    }
}
