package edu.kit.fallob.mallobio.output.distributors;

import java.util.ArrayList;
import java.util.List;

import edu.kit.fallob.mallobio.listeners.resultlisteners.ResultObjectListener;
import edu.kit.fallob.mallobio.outputupdates.ResultAvailableObject;

public class TestResultListener implements ResultObjectListener {
	
	List<ResultAvailableObject> procesedResultObejcts;
	
	public TestResultListener() {
		procesedResultObejcts = new ArrayList<>();
	}

	@Override
	public void processResultObject(ResultAvailableObject rao) {
		procesedResultObejcts.add(rao);
	}
}
