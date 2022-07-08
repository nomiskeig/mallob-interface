package edu.kit.fallob.mallobio.outputupdates;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class Event extends OutputUpdate {
	

	//TODO
	public static final String EVENT_REGEX = "";
	
	//event-attributes 
	private int processID;
	private int treeIndex;
	private int jobID;
	private boolean load;

	/**
	 * Constructor of event 
	 * @param logLine which has to match the Event.EVENT_REGEX
	 */
	public Event(String logLine) {
		super(logLine);
		setEventAttributes(logLine);
	}
	
	/**
	 * Parsees the logLine and sets the attributes of the event
	 */
	private void setEventAttributes(String logLine) throws IllegalArgumentException {
		//TODO
	}
	
	//-----------------------------------------getter

	public boolean isLoad() {
		return load;
	}

	public int getJobID() {
		return jobID;
	}

	public int getTreeIndex() {
		return treeIndex;
	}

	public int getProcessID() {
		return processID;
	}

}
