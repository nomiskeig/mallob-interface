package edu.kit.fallob.mallobio.output.distributors;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


import edu.kit.fallob.mallobio.listeners.outputloglisteners.OutputLogLineListener;
import edu.kit.fallob.mallobio.output.OutputProcessor;

public class OutputLogLineDistributor implements OutputProcessor {
	
	
	private List<OutputLogLineListener> listeners;
	
	public OutputLogLineDistributor() {
		this.listeners = new CopyOnWriteArrayList<>();
	}

	public void addListener(OutputLogLineListener listener) {
		listeners.add(listener);
	}

	public void removeListener(OutputLogLineListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void processLogLine(String logLine) {
		for (OutputLogLineListener l : listeners) {
			l.processLine(logLine);
		}
	}

}
