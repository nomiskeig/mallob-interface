package edu.kit.fallob.mallobio.output;

import java.util.ArrayList;
import java.util.List;

import edu.kit.fallob.mallobio.listeners.resultlisteners.ResultObjectListener;
import edu.kit.fallob.mallobio.output.distributors.ResultObjectDistributor;
import edu.kit.fallob.mallobio.outputupdates.ResultAvailableObject;

public class TestDistributor extends ResultObjectDistributor {
	
	
	List<String> resultPaths;
	int storedResults;
	
	public TestDistributor() {
		this.resultPaths = new ArrayList<>();
		updateStoredResults();
	}
	
	private void updateStoredResults() {
		this.storedResults = resultPaths.size();
	}
	
	@Override
	public void distributeResultObject(ResultAvailableObject rao) {
		this.resultPaths.add(rao.getFilePathToResult());
		System.out.println(rao.getFilePathToResult());
		updateStoredResults();
	}
	
	public void out(String s) {
		System.out.println(s);
	}

	public void resetResultPaths() {
		this.resultPaths = new ArrayList<>();
		updateStoredResults();
	}
}
