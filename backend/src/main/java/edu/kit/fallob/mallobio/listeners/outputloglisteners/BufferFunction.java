package edu.kit.fallob.mallobio.listeners.outputloglisteners;

/**
 * 
 * @author Simon Wilhelm Schübel
 *
 * @param <T> Object-parameter for the function
 */
public interface BufferFunction<T> {
	
	/**
	 * This is the function the Buffer-class is trying to execute 
	 * 
	 * @param outputUpdate
	 * @return true, if the execution of the function with the given parameter was successful, false if not
	 * @see Buffer-class for further context of this function  
	 */
	boolean bufferFunction(T outputUpdate);

}
