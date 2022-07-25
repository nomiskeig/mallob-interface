package edu.kit.fallob.mallobio.output;

public class TestActionChecker implements MallobOutputActionChecker {
	
	public boolean isDone = false;

	@Override
	public void checkForAction() {
		return;
	}

	@Override
	public boolean isDone() {
		return isDone;
	}

}
