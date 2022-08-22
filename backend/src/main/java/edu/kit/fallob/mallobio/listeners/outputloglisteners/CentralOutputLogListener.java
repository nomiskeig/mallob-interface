package edu.kit.fallob.mallobio.listeners.outputloglisteners;

public class CentralOutputLogListener implements OutputLogLineListener {

	@Override
	public void processLine(String line) {
		System.out.println(line);
	}

	
}
