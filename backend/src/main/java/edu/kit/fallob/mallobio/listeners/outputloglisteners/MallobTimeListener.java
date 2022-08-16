package edu.kit.fallob.mallobio.listeners.outputloglisteners;

public class MallobTimeListener implements OutputLogLineListener {
	
	
	private static final String LOGLINE_SEPARATOR = " ";
	private double secondsSinceMallobStart;

	
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
