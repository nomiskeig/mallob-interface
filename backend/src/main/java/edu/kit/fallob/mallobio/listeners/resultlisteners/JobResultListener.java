package edu.kit.fallob.mallobio.listeners.resultlisteners;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.Buffer;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.BufferFunction;

import org.json.JSONException;
import org.json.JSONObject;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.dataobjects.JobResult;
import edu.kit.fallob.dataobjects.ResultMetaData;
import edu.kit.fallob.mallobio.outputupdates.ResultAvailableObject;
import edu.kit.fallob.springConfig.FallobException;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 * 
 *          Writes Job-Result into the database
 *
 */
public class JobResultListener implements ResultObjectListener, BufferFunction<ResultAvailableObject> {

    private static final String INTERNAL_ID_KEY = "internal_id";
    private static final String PARSING_KEY = "parsing";
    private static final String SCHEDULING_KEY = "scheduling";
    private static final String PROCESSING_KEY = "processing";
    private static final String TOTAL_KEY = "total";
    private static final String USED_CPU_SECONDS_KEY = "used_cpu_seconds";
    private static final String USED_WALLCLOCK_SECONDS_KEY = "used_wallclock_seconds";

    private static final String STATS_KEY = "stats";
    private static final String TIME_KEY = "time";
	private static final String RESULT_KEY = "result";

	private static final String DIR_SEPARATOR = "/";
	

	private final FallobConfiguration configuration;
	private JobDao jobDao;
	private final Buffer<ResultAvailableObject> buffer;
	
	public JobResultListener(JobDao dao) {
        this.configuration = FallobConfiguration.getInstance();
        this.jobDao = dao;
        this.buffer = new Buffer<>(this);
    }


    @Override
    public void processResultObject(ResultAvailableObject rao) {
        this.buffer.bufferObject(rao);
        this.buffer.retryBufferedFunction(false);
    }

    private void saveResult(ResultAvailableObject rao, JSONObject result, int jobId) {
        //create the new result file
        File resultFile = new File(this.configuration.getResultBasePath() + DIR_SEPARATOR + rao.getResult().getName());

        //get the data from the json object and write it to the file
        JSONObject newResultObject = new JSONObject();
        newResultObject.put(RESULT_KEY, result.getJSONObject(RESULT_KEY));

        try {
            FileWriter writer = new FileWriter(resultFile);
            writer.write(newResultObject.toString(2));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JobResult newJobResult = new JobResult(resultFile);

        //remove the old result file from the filesystem
        rao.getResult().delete();

        JSONObject stats = (JSONObject) result.get(STATS_KEY);
        JSONObject time = (JSONObject) stats.get(TIME_KEY);

        double parsingTime = (double) time.get(PARSING_KEY);
        double processingTime = (double) time.get(PROCESSING_KEY);
        double schedulingTime = (double) time.get(SCHEDULING_KEY);
        double totalTime = (double) time.get(TOTAL_KEY);
        double usedCpuSeconds = (double) stats.get(USED_CPU_SECONDS_KEY);
        double usedWallclockSeconds = (double) stats.get(USED_WALLCLOCK_SECONDS_KEY);

        ResultMetaData newResultMetaData = new ResultMetaData(parsingTime,
                processingTime, schedulingTime, totalTime, usedCpuSeconds, usedWallclockSeconds);
        try {
            jobDao.addJobResult(jobId, newJobResult, newResultMetaData);
        } catch (FallobException e) {
            // TODO Auto-generated catch block
            System.out.println("Job result of job with id " + jobId + " could not be saved.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean bufferFunction(ResultAvailableObject outputUpdate) {
        String jsonString = null;
        try {
            jsonString = Files.readString(Path.of(outputUpdate.getFilePathToResult()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject result;
        // technically this try-catch block should not be required but it seems that
        // sometimes the file is read before the contents are written and this is the
        // easiest way to catch that error.
        try {
            result = new JSONObject(jsonString);
        } catch (JSONException e) {
            return false;
        }

        int mallobID = (int) result.get(INTERNAL_ID_KEY);

        int jobId = 0;
        try {
            jobId = this.jobDao.getJobIdByMallobId(mallobID);
        } catch (FallobException e) {
            e.printStackTrace();
            System.out.println("An sql error occurred while accessing the database");
        }

        if (jobId > 0) {
            this.saveResult(outputUpdate, result, jobId);
            return true;
        }
        return false;
    }
}
