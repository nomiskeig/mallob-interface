package edu.kit.fallob.mallobio.distributors;

import edu.kit.fallob.mallobio.listeners.outputloglisteners.OutputLogLineListener;

import java.util.List;
import java.util.ArrayList;

public class TestListener implements OutputLogLineListener {
	
	public  List<String> logLines;
	
	public TestListener() {
		this.logLines = new ArrayList<>();
	}

	@Override
	public void processLine(String line) {
		this.logLines.add(line);
	}

}
