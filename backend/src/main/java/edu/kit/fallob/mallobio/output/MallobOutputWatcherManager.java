package edu.kit.fallob.mallobio.output;

import java.util.List;

import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.mallobio.MallobFilePathGenerator;
import edu.kit.fallob.mallobio.output.distributors.ResultObjectDistributor;

public class MallobOutputWatcherManager {
	
	private static MallobOutputWatcherManager manager;
	private ResultObjectDistributor resultDistributor;
	
	
	private List<MallobClientOutputWatcher> watchers;
	private List<Thread> watcherThreads;
	
	
	public static MallobOutputWatcherManager getInstance() {
		if (manager == null) {
			manager = new MallobOutputWatcherManager();
		}
		return manager;
	}
	
	
	private MallobOutputWatcherManager() {}

	
	/**
	 * Sets the result distributor to resultDistributor for all watchers being created from this point 
	 * @param resultDistributor
	 */
	public void setResultDistributor(ResultObjectDistributor resultDistributor) {
		if (resultDistributor == null) {
			throw new NullPointerException("Cannot set result distributor to null.");
		}
		this.resultDistributor = resultDistributor;
	}
	
	
	/**
	 * Creates a watcher to watch for a result of a given job, submitted by a given user
	 * 
	 * @param userName username of the user who submitted the job
	 * @param jobName name of the job the user gave to it 
	 * @param clientProcessID ProcessID of the (client) process which received the job
	 * 
	 * @throws NullPointerException if no result distributor is available for watcher-thread
	 */
	 /// .api/jobs.0/out/<user-name>.<job-name>.json
	public void addNewWatcher(String userName, String jobName, int clientProcessID) {
		
		if (this.resultDistributor == null) {
			throw new NullPointerException("No result distributor available for distribution.");
		}
		String pathToOutputDirectpory = 
				MallobFilePathGenerator.generateOutDirectoryPath(clientProcessID, (FallobConfiguration.getInstance()).getMallobBasePath());
		String expectedResultName = MallobFilePathGenerator.generateResultName(jobName, userName);
		MallobClientOutputWatcher watcher = new MallobClientOutputWatcher(pathToOutputDirectpory, expectedResultName);
		watcher.setDistributor(resultDistributor);
		watchers.add(watcher);
		
		
		startWatcherThread(watcher);
	}


	/**
	 * Creates a thread that holds a watcher and starts the thread immediately 
	 * 
	 * @param watcher
	 */
	private void startWatcherThread(MallobClientOutputWatcher watcher) {
		Thread t = new Thread(watcher);
		this.watcherThreads.add(t);
		t.start();
	}
}