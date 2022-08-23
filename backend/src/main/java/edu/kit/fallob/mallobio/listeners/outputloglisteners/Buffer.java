package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 
 * @author Simon Wilhelm Schübel
 * 
 * This buffer holds objects, until a given function can be executed with those object as parameters
 *
 * @param <T> Type of object which is supposed to be buffered 
 */
public class Buffer<T> {
	
	private Queue<T> bufferedUpdates;
	private BufferFunction<T> bufferFunction;
	
	
	public Buffer(BufferFunction<T> f) {
		bufferedUpdates = new LinkedList<>();
		bufferFunction = f;
	}
	
	/**
	 * Runs through all buffered objects and tries to execute the buffer function.
	 * If execution was successful, the object is removed, if not the object is stored until 
	 * this function is called again
	 */
	public void retryBufferedFunction() {
		if (bufferedUpdates.size() == 0) {return;}
		int maxTries = bufferedUpdates.size();
		
		while(maxTries > 0) {
			T update = bufferedUpdates.poll();
			if (update == null) {
				tryToExecuteBufferFunciton(update);
			}
			maxTries--;
		}
	}
	
	/**
	 * Buffer events in a linked list
	 * 
	 * @param outputUpdate 
	 */
	public void bufferObject(T outputUpdate) {
		try {
			bufferedUpdates.add(outputUpdate);
		} catch(IllegalStateException e) {
			System.out.println("Event could not be added to buffering-queue : capacity overflow.");
		}
	}	
	
	/**
	 * This function tries to execute the given bufferFunction. If the function is succeessful - means it returns true,
	 * the object is not buffered
	 * 
	 * If the function fails for some reason, the object is buffered. Function can be re-exectued by calling retryBufferedFunction
	 * 
	 * @param outputUpdate - the usual objects, who are being buffered. The given bufferedFunctio is executed with the given object
	 * If it returns false, the obejct is now buffered.
	 */
	public void tryToExecuteBufferFunciton(T outputUpdate) {
		if (!this.bufferFunction.bufferFunction(outputUpdate)) {
			bufferObject(outputUpdate);
		} 
	}
}
