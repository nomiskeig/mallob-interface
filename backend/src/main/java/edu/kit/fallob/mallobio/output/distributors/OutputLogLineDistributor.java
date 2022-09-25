package edu.kit.fallob.mallobio.output.distributors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


import edu.kit.fallob.mallobio.listeners.outputloglisteners.OutputLogLineListener;
import edu.kit.fallob.mallobio.output.OutputProcessor;

public class OutputLogLineDistributor implements OutputProcessor {
	
	
	private List<OutputLogLineListener> listeners;
	
	public OutputLogLineDistributor() {
		this.listeners = Collections.synchronizedList(new ArrayList<>());
	}

	public void addListener(OutputLogLineListener listener) {
		listeners.add(listener);
	}

	public void removeListener(OutputLogLineListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void processLogLine(String logLine) {
		synchronized(listeners) {
			Iterator<OutputLogLineListener> i = listeners.iterator();
			while (i.hasNext()) {
				i.next().processLine(logLine);
			}
		}
	}

}
