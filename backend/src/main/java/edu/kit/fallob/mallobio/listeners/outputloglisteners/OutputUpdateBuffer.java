package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import java.util.LinkedList;
import java.util.Queue;

public class OutputUpdateBuffer<T> {
	
	private Queue<T> bufferedUpdates;
	private BufferFunction<T> bufferFunction;
	
	
	public OutputUpdateBuffer(BufferFunction<T> f) {
		bufferedUpdates = new LinkedList<>();
		bufferFunction = f;
	}
	
	/**
	 * Runs through all buffered events and tries to re-store them to the database 
	 */
	public void retrySavingBufferedObjects() {
		if (bufferedUpdates.size() == 0) {return;}
		int maxTries = bufferedUpdates.size();
		T update = bufferedUpdates.poll();
		
		while(update != null && maxTries > 0) {
			tryToExecuteBufferFunciton(update);
			maxTries--;
			update = bufferedUpdates.poll();
		}
	}
	
	public void bufferEvent(T outputUpdate) {
		try {
			bufferedUpdates.add(outputUpdate);
		} catch(IllegalStateException e) {
			System.out.println("Event could not be added to buffering-queue : capacity overflow.");
		}
	}	
	
	/*
	 * Tries to convert the mallob-id of the event to the fallob job-id 
	 * and if it fails, it buffers the event via bufferEvent(event)
	 */
	public void tryToExecuteBufferFunciton(T outputUpdate) {
		if (!this.bufferFunction.bufferFunction(outputUpdate)) {
			bufferEvent(outputUpdate);
		} 
	}
}
