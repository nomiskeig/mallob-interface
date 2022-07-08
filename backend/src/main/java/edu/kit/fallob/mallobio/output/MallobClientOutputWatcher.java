package edu.kit.fallob.mallobio.output;

import edu.kit.fallob.mallobio.output.distributors.ResultObjectDistributor;
import edu.kit.fallob.mallobio.outputupdates.ResultAvailableObject;

public class MallobClientOutputWatcher {
	
	private String pathToMallobDirectory;
	
	private int clientProcessRank;
	
	private ResultObjectDistributor distributor;
	
	
	
	public MallobClientOutputWatcher(String pathToMallobDirectory) {
		this.pathToMallobDirectory = pathToMallobDirectory;
	}
	
	public void watchDirectory() {
		
	}
	
	private void pushResultObject(ResultAvailableObject rao) {
		this.distributor.distributeResultObject(rao);
	}
}
