package edu.kit.fallob.mallobio.listeners.outputloglisteners;

public class MallobTimeListener implements OutputLogLineListener {
	
	//public static final Strin
	
	private float secondsSinceMallobStart;

	
	public float getAmountOfSecondsSinceStart() {
		return this.secondsSinceMallobStart;
	}


	@Override
	public void processLine(String line) {
		//TODO : Parse line and get time, override float sinceMallobStart 
	}
}
