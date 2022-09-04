package edu.kit.fallob.mallobio.listeners.resultlisteners;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.kit.fallob.mallobio.listeners.outputloglisteners.Buffer;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.BufferFunction;
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
 * Writes Job-Result into the database 
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
	
	
	private JobDao jobDao;
	private final Buffer<ResultAvailableObject> buffer;
	
	public JobResultListener(JobDao dao) {
		this.jobDao = dao;
		this.buffer = new Buffer<>(this);

	}

	@Override
	public void processResultObject(ResultAvailableObject rao) {
		this.buffer.tryToExecuteBufferFunciton(rao);

		this.buffer.retryBufferedFunction();
	}

	private void saveResult(ResultAvailableObject rao, JSONObject result, int jobId) {
		JobResult newJobResult = new JobResult(rao.getResult());

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
		JSONObject result = new JSONObject(jsonString);
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
