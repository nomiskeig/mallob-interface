package edu.kit.fallob.mallobio.output;

import edu.kit.fallob.mallobio.output.distributors.ResultObjectDistributor;

public class MallobOutputWatcherManager {
	
	private static MallobOutputWatcherManager manager;
	private ResultObjectDistributor resulDistributor;
	
	private MallobOutputRunnerThread[] watcherThreads;
	private Thread[] threadpool;
	
	
	public static MallobOutputWatcherManager getInstance() {
		if (manager == null) {
			manager = new MallobOutputWatcherManager();
		}
		return manager;
	}
	
	
	private MallobOutputWatcherManager() {}
	
	public MallobOutputWatcherManager(String mallobBaseDirectory, int amountWatcherThreads,
			int watchingIntervalPerWatcherThread) 
	{
		
		
	}

	
	

	public void setResultDistributor(ResultObjectDistributor resulDistributor) {
		this.resulDistributor = resulDistributor;
	}
	
	
	/**
	 * Creates a watcher to watch for a result of a given job, submitted by a given user
	 * 
	 * @param userName username of the user who submitted the job
	 * @param jobName name of the job the user gave to it 
	 * @param clientProcessID ProcessID of the (client) process which received the job
	 */
	 /// .api/jobs.0/out/<user-name>.<job-name>.json
	public void addNewWatcher(String userName, String jobName, int clientProcessID) {
		String pathToOutputDirectory = ".api/jobs." + Integer.toString(clientProcessID) + "/out/";
		MallobClientOutputWatcher watcher = new MallobClientOutputWatcher(pathToOutputDirectory);
		watcher.setDistributor(resulDistributor);
		addWatcherToThreadPool(watcher);
	}


	/**
	 * Adds a watcher to the threadpool
	 * @param watcher
	 */
	private void addWatcherToThreadPool(MallobClientOutputWatcher watcher) {
		watcherThreads[getIndexOfMinimalLoadThread()].addActionChecker(watcher);
	}
	
	/**
	 *O(n), where n is the amount of watcher-threads
	 * @return
	 */
	private int getIndexOfMinimalLoadThread() {
		int minAmountWatchers = Integer.MAX_VALUE;
		for (int i = 0; i < threadpool.length; i++) {
			if (watcherThreads[i].getAmountActionCheckers() <= minAmountWatchers) {
				minAmountWatchers = i;
			}
		}
		return minAmountWatchers;
	}

	
	//--------------------------------setup and start of threads

	public void setup(String mallobBaseDirectory, int amountWatcherThreads, int watchingIntervalPerWatcherThread) {
		watcherThreads = new MallobOutputRunnerThread[amountWatcherThreads];
		threadpool = MallobOutputRunnerThread.initializeThreadPool(watcherThreads, watchingIntervalPerWatcherThread);
	}
	
	public void startThreads() {
		MallobOutputRunnerThread.startThreadPoolExecution(threadpool);
	}
	
	


	public void stopThreads() throws IllegalArgumentException, InterruptedException {
		MallobOutputRunnerThread.stopThreadPoolExecution(threadpool, watcherThreads);
	}

	//-------------------------------other funcionality 

}
