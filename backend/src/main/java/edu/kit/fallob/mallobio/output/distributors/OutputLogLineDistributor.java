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
        this.listeners = new CopyOnWriteArrayList<>();
    }

    public synchronized void addListener(OutputLogLineListener listener) {
            listeners.add(listener);
    }

    public synchronized void removeListener(OutputLogLineListener listener) {
            listeners.remove(listener);
    }

    @Override
    public synchronized void processLogLine(String logLine) {
            Iterator<OutputLogLineListener> i = listeners.iterator();
            while (i.hasNext()) {
                i.next().processLine(logLine);
            }
    }

}
