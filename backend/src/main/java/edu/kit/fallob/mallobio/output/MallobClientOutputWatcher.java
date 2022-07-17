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
		scanInitialFiles();
	}
	
	public MallobClientOutputWatcher(String pathToMallobDirectory) {
		this.pathToMallobDirectory = pathToMallobDirectory;
		this.processedResults = new ArrayList<>();
		scanInitialFiles();
	}
	
	

	
	
	/**
	 * This method scans the given directory for initial files,
	 * such that these files can be distinguished from results
	 */
	private void scanInitialFiles() {
		File directrory = new File(pathToMallobDirectory);
		List<File> files = new ArrayList<>(Arrays.asList(directrory.listFiles()));
		for (File f : files) {
			processedResults.add(f.getAbsolutePath());
		}
	}
	
	/**
	 * Check for changes in the directory, given in the creation of the file
	 */
	public void watchDirectory() {
		File directrory = new File(pathToMallobDirectory);
		//List<File> files = new ArrayList<>(Arrays.asList(directrory.listFiles()));
		List<File> files = List.of(directrory.listFiles());

		//filter the files that have already been processed
		
		List<String> processedFiles = new ArrayList<>();
		
		//check if they have been processed: if yes, add them to the processedFiles list
		for (File f : files) {
			if (processedResults.contains(f.getAbsolutePath())) {
				processedFiles.add(f.getAbsolutePath());
			}
		}
		
		
		//now, files only contain files that have not yet been processed
		for (File f : files) {
			if (!processedFiles.contains(f.getAbsolutePath())) {
				processedResults.add(f.getAbsolutePath());
				pushResultObject(new ResultAvailableObject(f));
			}
		}
	}
	

	private void pushResultObject(ResultAvailableObject rao) {
		this.distributor.distributeResultObject(rao);
	}

	@Override
	public void checkForAction() {
		watchDirectory();
	}
	
	public void setDistributor(ResultObjectDistributor distributor) {
		this.distributor = distributor;
	}
}
