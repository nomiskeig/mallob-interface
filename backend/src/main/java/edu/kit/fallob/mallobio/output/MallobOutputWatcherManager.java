package edu.kit.fallob.mallobio.output;

import java.util.List;

import edu.kit.fallob.mallobio.output.distributors.ResultObjectDistributor;

public class MallobOutputWatcherManager {
	
	private static MallobOutputWatcherManager manager;
	private ResultObjectDistributor resulDistributor;
	
	private List<MallobClientOutputWatcher> watchers;
	private List<Thread> watcherThreads;
	
	
	public static MallobOutputWatcherManager getInstance() {
		if (manager == null) {
			manager = new MallobOutputWatcherManager();
		}
		return manager;
	}
	
	
	private MallobOutputWatcherManager() {}



	
	

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
