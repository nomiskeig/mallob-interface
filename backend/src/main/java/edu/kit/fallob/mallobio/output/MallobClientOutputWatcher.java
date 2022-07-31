package edu.kit.fallob.mallobio.output;

import edu.kit.fallob.mallobio.output.distributors.ResultObjectDistributor;
import edu.kit.fallob.mallobio.outputupdates.ResultAvailableObject;


import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 * 
 * 
 * This class watches for changes in ONE directory.  If a new file is detected,
 * it is first checked if the file has already been processed.
 * 
 * If not, the file is then processed
 *
 */
public class MallobClientOutputWatcher implements Runnable{
	
	private String pathToMallobDirectory;
		
	private ResultObjectDistributor distributor;
		
	private String expectedResultName;
	private boolean retreivedResult;
	private boolean isWatching;
	
	
	
	
	public MallobClientOutputWatcher(String pathToMallobDirectory, String expectedResultName) {
		setupClientOutputWatcher(pathToMallobDirectory);
		this.expectedResultName = expectedResultName;
	}
	
	
	private void setupClientOutputWatcher(String pathToMallobDirectory) {
		this.pathToMallobDirectory = pathToMallobDirectory;
		retreivedResult = false;
		isWatching = false;
		//scanInitialFiles();
	}
	
	/**
	 * Check for changes in the directory, given in the creation of the file
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public void watchDirectory() throws IOException, InterruptedException {
		
		WatchService watcher = getWatcher();

		//retreive the result from the directory 
		while(!retreivedResult) {
			this.isWatching = true;
			WatchKey nextKey = watcher.take();
			
			
			for (WatchEvent<?> event : nextKey.pollEvents()) {
				
				if (event.kind() != StandardWatchEventKinds.ENTRY_CREATE) {
					continue;
				}
								
				WatchEvent<Path> ev = (WatchEvent<Path>)event;
		        Path filename = ev.context();
		        		        
				if (isResult(filename.toString())){
			        this.pushResultObject(new ResultAvailableObject(this.pathToMallobDirectory + filename.toString()));
			        retreivedResult = true;
				}
			}
		}
		
	}
	
	
	/**
	 * Decides weather an event, detected by the WathcServie is the creation of the result file.
	 * If yes, it returns true, if no false
	 * @param string
	 * @return checks if the given event is the result file
	 */
	private boolean isResult(String filename) {
		if (filename.equals(this.expectedResultName)) {
			return true;
		}
		return false;
	}

	/**
	 * Creates a watcher for the directory-path stored in pathToMallobDirectory.
	 * Also registers create-events in this directory.
	 * 
	 * @return the watcher for the specified directory
	 * @throws IOException
	 */
	private WatchService getWatcher() throws IOException {
		//setup watcher 
		Path dir = Paths.get(pathToMallobDirectory);
		WatchService watcher = FileSystems.getDefault().newWatchService();
		try {
			dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
		} catch(IOException s) {
			s.printStackTrace();
		}
		return watcher;
	}
	

	private void pushResultObject(ResultAvailableObject rao) {
		this.distributor.distributeResultObject(rao);
	}
	
	
	public void setDistributor(ResultObjectDistributor distributor) {
		this.distributor = distributor;
	}


	@Override
	public void run() {
		try {
			watchDirectory();
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return true if the watcher is watching the directory 
	 */
	public boolean isWatching() {
		return isWatching;
	}
	
	public boolean isDone() {
		return retreivedResult;
	}
}
