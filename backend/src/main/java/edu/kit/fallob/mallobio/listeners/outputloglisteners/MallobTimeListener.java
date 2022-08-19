package edu.kit.fallob.mallobio.listeners.outputloglisteners;

public class MallobTimeListener implements OutputLogLineListener {
	
	//public static final Strin
	
	private float secondsSinceMallobStart;
	
	private static MallobTimeListener instance;
	
	
	public static MallobTimeListener getInstance(){
		if (instance == null) {
			instance = new MallobTimeListener();
		}
		return instance;
	}
	
	private MallobTimeListener() {
		
	}
	

	
	public float getAmountOfSecondsSinceStart() {
		return this.secondsSinceMallobStart;
	}


	@Override
	public void processLine(String line) {
		//TODO : Parse line and get time, override float sinceMallobStart 
	}
}
