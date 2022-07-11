package edu.kit.fallob.mallobio.output;

import edu.kit.fallob.mallobio.output.distributors.ResultObjectDistributor;
import edu.kit.fallob.mallobio.outputupdates.ResultAvailableObject;

import java.util.List;
import java.io.File;
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
public class MallobClientOutputWatcher implements MallobOutputActionChecker{
	
	private String pathToMallobDirectory;
	
	private int clientProcessRank;
	
	private ResultObjectDistributor distributor;
	
	private List<String> processedResults;
	
	
	
	public MallobClientOutputWatcher(String pathToMallobDirectory, int clientProcessID) {
		this.pathToMallobDirectory = pathToMallobDirectory;
		this.clientProcessRank = clientProcessID;
		this.processedResults = new ArrayList<>();
	}
	
	/**
	 * Check for changes in the directory, given in the creation of the file
	 */
	public void watchDirectory() {
		File directrory = new File(pathToMallobDirectory);
		List<File> files = new ArrayList<>(Arrays.asList(directrory.listFiles()));
		
		//filter the files that have already been processed
		
		List<File> processedFiles = new ArrayList<>();
		//check if they have been processed
		for (File f : files) {
			for (String path : processedResults) {
				if (path.equals(f.getAbsolutePath())) {
					//file has already been processed
					processedFiles.add(f);
				}
			}
		}
		
		for (File f : processedFiles) {
			files.remove(f);
		}
		
		//now, files only contain files that have not yet been processed
		for (File f : files) {
			processedResults.add(f.getAbsolutePath());
			pushResultObject(new ResultAvailableObject(f));
		}
	}
	
	private void pushResultObject(ResultAvailableObject rao) {
		this.distributor.distributeResultObject(rao);
	}

	@Override
	public void checkForAction() {
		watchDirectory();
	}
}
