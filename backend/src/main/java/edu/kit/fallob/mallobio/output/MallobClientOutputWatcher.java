package edu.kit.fallob.mallobio.output;

import edu.kit.fallob.mallobio.output.distributors.ResultObjectDistributor;
import edu.kit.fallob.mallobio.outputupdates.ResultAvailableObject;

import java.util.List;
import java.util.ArrayList;

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
public class MallobClientOutputWatcher {
	
	private String pathToMallobDirectory;
	
	private int clientProcessRank;
	
	private ResultObjectDistributor distributor;
	
	private List<String> processedResults;
	
	
	
	public MallobClientOutputWatcher(String pathToMallobDirectory) {
		this.pathToMallobDirectory = pathToMallobDirectory;
		this.processedResults = new ArrayList<>();
	}
	
	
	public void watchDirectory() {
		
	}
	
	private void pushResultObject(ResultAvailableObject rao) {
		this.distributor.distributeResultObject(rao);
	}
}
