package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.mallobio.outputupdates.StatusUpdate;
import edu.kit.fallob.springConfig.FallobException;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class JobStatusListener implements OutputLogLineListener, BufferFunction<StatusUpdate> {

	
	private JobDao jobDao;
	private final Buffer<StatusUpdate> buffer;
	
	public JobStatusListener() {
		DaoFactory daoFactory = null;
		try {
			daoFactory = new DaoFactory();
		} catch (FallobException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jobDao = daoFactory.getJobDao();

		this.buffer = new Buffer<>(this);
	}

	@Override
	public void processLine(String line) {
		if (StatusUpdate.isJobStatus(line)) {
            System.out.println(line);
			StatusUpdate statusUpdate = new StatusUpdate(line);

			this.buffer.tryToExecuteBufferFunciton(statusUpdate);
		}

		//retry to save buffered updates
		this.buffer.retryBufferedFunction();
	}

	@Override
	public boolean bufferFunction(StatusUpdate outputUpdate) {
		int jobId = 0;
		try {
			jobId = this.jobDao.getJobIdByMallobId(outputUpdate.getJobID());
		} catch (FallobException e) {
			e.printStackTrace();
			System.out.println("An sql error occurred while accessing the database");
		}

		if (jobId > 0) {
			try {
				this.jobDao.updateJobStatus(jobId, outputUpdate.getJobStatus());
			} catch (FallobException e) {
				System.out.println("Job status could not be updated: " + e.getMessage());
			}
			return true;
		}
		return false;
	}
}
