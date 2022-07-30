package edu.kit.fallob.mallobio.output;

import edu.kit.fallob.mallobio.output.distributors.ResultObjectDistributor;
import edu.kit.fallob.mallobio.outputupdates.ResultAvailableObject;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;

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
	
	private List<String> processedResults;
	
	private boolean retreivedResult;
	
	
	
	
	public MallobClientOutputWatcher(String pathToMallobDirectory) {
		System.out.println(pathToMallobDirectory);
		setupClientOutputWatcher(pathToMallobDirectory);
	}
	
	
	private void setupClientOutputWatcher(String pathToMallobDirectory) {
		this.pathToMallobDirectory = pathToMallobDirectory;
		this.processedResults = new ArrayList<>();
		retreivedResult = false;
		//scanInitialFiles();
	}
	
	

	
	
	/**
	 * This method scans the given directory for initial files,
	 * such that these files can be distinguished from results
	 * 
	 * The idea of this method is to scan files that have been in the directory beforehand, 
	 * such that the Output-Watcher only pushes newly created/moved files to the distributor 
	 * 
	 
	private void scanInitialFiles() {
		File directrory = new File(pathToMallobDirectory);
		List<File> files = new ArrayList<>(Arrays.asList(directrory.listFiles()));
		for (File f : files) {
			processedResults.add(f.getAbsolutePath());
		}
	}
	
	*/
	
	/**
	 * Check for changes in the directory, given in the creation of the file
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public void watchDirectory() throws IOException, InterruptedException {
		
		WatchService watcher = getWatcher();
		//retreive the result from the directory 
		while(!retreivedResult) {
			WatchKey nextKey = watcher.take();
			for (WatchEvent<?> event : nextKey.pollEvents()) {
				
				if (isResult(event)){
					WatchEvent<Path> ev = (WatchEvent<Path>)event;
			        Path filename = ev.context();
			        retreivedResult = true;
			        this.pushResultObject(new ResultAvailableObject(filename.toAbsolutePath().toString()));
				}
			}
		}
		
	}
	
	
	/**
	 * Decides weather an event, detected by the WathcServie is the creation of the result file.
	 * If yes, it returns true, if no false
	 * @param event
	 * @return checks if the given event is the result file
	 */
	private boolean isResult(WatchEvent<?> event) {
		if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
			return true;
		}
		return false;
	}


	private WatchService getWatcher() throws IOException {
		//setup watcher 
		Path dir = Paths.get(pathToMallobDirectory);
		WatchService watcher = FileSystems.getDefault().newWatchService();
		try {
			WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
		} catch(IOException s) {
			
		}
		return watcher;
	}
	

	private void pushResultObject(ResultAvailableObject rao) {
		this.distributor.distributeResultObject(rao);
		this.retreivedResult = true;
	}
	
	public boolean isDone() {
		return retreivedResult;
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
}
