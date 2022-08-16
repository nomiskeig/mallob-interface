package edu.kit.fallob.mallobio.listeners.outputloglisteners;

public class MallobTimeListener implements OutputLogLineListener {
	
	
<<<<<<< HEAD
	private static final String LOGLINE_SEPARATOR = " ";
	private double secondsSinceMallobStart;
=======
	private double secondsSinceMallobStart;
	
	private static MallobTimeListener instance;
	
	
	public static MallobTimeListener getInstance(){
		if (instance == null) {
			instance = new MallobTimeListener();
		}
		return instance;
	}
	
	private MallobTimeListener() {
		
	}
	
>>>>>>> MallobIOIntegration

	
	public double getAmountOfSecondsSinceStart() {
		return this.secondsSinceMallobStart;
	}


	@Override
	public void processLine(String line) {
		//TODO : Parse line and get time, override float sinceMallobStart 
		String[] splittedLogLine = line.split(LOGLINE_SEPARATOR);
		double updatedSecondsSinceMallobStart = Double.parseDouble(splittedLogLine[0]);
		if (updatedSecondsSinceMallobStart != secondsSinceMallobStart) {
			secondsSinceMallobStart = updatedSecondsSinceMallobStart;
		}
	}
}
