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
	 */
	public void watchDirectory() {
		if (retreivedResult) {
			return;
		}
		
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
		this.retreivedResult = true;
	}
	
	public boolean isDone() {
		return retreivedResult;
	}

	@Override
	public void checkForAction() {
		watchDirectory();
	}
	
	public void setDistributor(ResultObjectDistributor distributor) {
		this.distributor = distributor;
	}
}
