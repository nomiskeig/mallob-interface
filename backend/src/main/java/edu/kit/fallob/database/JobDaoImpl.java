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
import java.util.Arrays;
import java.util.List;

public class JobDaoImpl implements JobDao{

    private final Connection conn;
    private final FallobConfiguration configuration;

    private static final String SINGLE_FILE_REGEX = "%o\\..*";
    private static final String DESCRIPTION_FILES_REGEX = "%s\\d+\\..*";
    private static final String FILE_EXTENSION_REGEX = "\\.";
    private static final String DIRECTORY_SEPARATOR = "/";
    private static final String FILE_SEPARATOR = ".";
    private static final String NAME_SEPARATOR = "_";
    private static final String ARRAY_TYPE = "INT";

    private static final String JOB_INSERT = "INSERT INTO job (username, submissionTime, jobStatus, mallobId) VALUES (?, ?, ?, ?)";
    private static final String CONFIGURATION_INSERT = "INSERT INTO jobConfiguration (jobId, name, priority, application, maxDemand, wallclockLimit, cpuLimit, arrival, dependencies, incremental, precursor, contentMode, additionalConfig) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_JOB_ID = "UPDATE jobDescription SET jobId=? WHERE descriptionId=?";
    private static final String DESCRIPTION_INSERT = "INSERT INTO jobDescription (username, submitType, uploadTime) VALUES (?, ?, ?)";
    private static final String GET_OLDEST_JOB_DESCRIPTION = "SELECT descriptionId FROM jobDescription WHERE MIN(uploadTime)";
    private static final String DELETE_JOB_DESCRIPTION = "DELETE FROM jobDescription WHERE descriptionId=?";
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
    private static final String GET_JOBS_WITH_STATUS = "SELECT jobId FROM job WHERE jobStatus=?";

    public JobDaoImpl() {
        this.conn = DatabaseConnectionFactory.getConnection();
        this.configuration = FallobConfiguration.getInstance();
    }
    @Override
    public int saveJobConfiguration(JobConfiguration configuration, String username, int mallobId) {
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

            //add data to jobConfiguration table
            PreparedStatement configStatement = this.conn.prepareStatement(CONFIGURATION_INSERT);
            configStatement.setInt(1, jobId);
            configStatement.setString(2, configuration.getName());
            configStatement.setDouble(3, configuration.getPriority());
            configStatement.setString(4, configuration.getApplication());
            configStatement.setInt(5, configuration.getMaxDemand());
            configStatement.setString(6, configuration.getWallClockLimit());
            configStatement.setString(7, configuration.getCpuLimit());
            configStatement.setString(8, configuration.getArrival());
            //convert the dependencies list into an Array object
            Array dependencies = this.conn.createArrayOf(ARRAY_TYPE, configuration.getDependencies().toArray());
            configStatement.setArray(9, dependencies);
            configStatement.setBoolean(10, configuration.isIncremental());
            configStatement.setString(11, configuration.getPrecursor());
            //TODO: contentMode attribut fehlt
            //configStatement.setString(12, configuration.);

            configStatement.setString(13, configuration.getAdditionalParameter());

            configStatement.executeUpdate();

            //update the entry in the jobDescription table to set the jobId
            PreparedStatement updateStatement = this.conn.prepareStatement(UPDATE_JOB_ID);
            updateStatement.setInt(1, jobId);
            updateStatement.setInt(2, configuration.getDescriptionID());

            updateStatement.executeUpdate();

            return jobId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int saveJobDescription(JobDescription description, String username) {
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeOldestJobDescription() {
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
            throw new RuntimeException(e);
        }

        //remove the descriptionFiles from the filesystem
        String path = this.configuration.getDescriptionsbasePath();
        String regex = String.format(DESCRIPTION_FILES_REGEX, descriptionId);

        FileHandler.deleteFilesByRegex(path, regex);
    }

    @Override
    public void removeAllJobsBeforeTime(LocalDateTime time) {
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public int[] getAllJobIds(String username) {
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public JobDescription getJobDescription(int descriptionId) {
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
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JobConfiguration getJobConfiguration(int jobId) {
        int descriptionId = 0;
        try {
            PreparedStatement getDescriptionId = this.conn.prepareStatement(GET_DESCRIPTION_ID);
            getDescriptionId.setInt(1, jobId);

            ResultSet descriptionIdResult = getDescriptionId.executeQuery();
            if (descriptionIdResult.next()) {
                descriptionId = descriptionIdResult.getInt(1);
            } else {
                throw new RuntimeException();
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
                Integer[] dependenciesArray = (Integer[]) result.getArray(9).getArray();
                //convert array back into list of integers
                List<Integer> dependencies = new ArrayList<>(Arrays.asList(dependenciesArray));
                boolean incremental = result.getBoolean(10);
                String precursor = result.getString(11);
                String contentMode = result.getString(12);
                String additionalConfig = result.getString(13);

                //TODO: contentMode is missing
                return new JobConfiguration(name, priority, application, maxDemand, wallclockLimit, cpuLimit, arrival, dependencies, incremental, precursor, descriptionId, additionalConfig, contentMode);
            } else {
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JobStatus getJobStatus(int jobId) {
        try {
            PreparedStatement statement = this.conn.prepareStatement(GET_JOB_STATUS);
            statement.setInt(1, jobId);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String statusString = result.getString(1);
                return JobStatus.valueOf(statusString);
            } else {
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JobInformation getJobInformation(int jobId) {
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
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public JobResult getJobResult(int jobId) {
        String path = this.configuration.getResultBasePath();
        String regex = String.format(SINGLE_FILE_REGEX, jobId);
        List<File> resultFile = FileHandler.getFilesByRegex(path, regex);

        if (resultFile.size() == 1) {
            return new JobResult(resultFile.get(0));
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void updateJobStatus(int jobId, JobStatus status) {
        try {
            PreparedStatement statement = this.conn.prepareStatement(UPDATE_JOB_STATUS);
            statement.setString(1, status.name());
            statement.setInt(2, jobId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addJobResult(int jobId, JobResult result, ResultMetaData resultMetaData) {
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getJobIdByMallobId(int mallobId) {
        try {
            PreparedStatement statement = this.conn.prepareStatement(JOB_ID_BY_MALLOB_ID);
            statement.setInt(1, mallobId);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getInt(1);
            } else {
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getSizeOfAllJobDescriptions() {
        String path = this.configuration.getDescriptionsbasePath();
        return FileHandler.getSizeOfDirectory(path);
    }

    @Override
    public List<Integer> getAllRunningJobs() {
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
            throw new RuntimeException(e);
        }
    }


    private int getGeneratedKey(PreparedStatement statement) {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getInt(1);
            } else {
                throw new RuntimeException("Generated key couldn't be found!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFileExtension(File file) {
        String[] fileNameSplit = file.getName().split(FILE_EXTENSION_REGEX);
        //get the index of the last array element
        int filExtensionIndex = fileNameSplit.length - 1;

        return fileNameSplit[filExtensionIndex];
    }

    private ResultMetaData getResultMetaData(int jobId) {
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
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
