package edu.kit.fallob.mallobio.outputupdates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.sql.Date;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class Event extends OutputUpdate {
	

	public static final String EVENT_REGEX = OutputUpdate.TIME_REGEX + OutputUpdate.REGEX_SEPARATOR + "LOAD" + OutputUpdate.REGEX_SEPARATOR + "[0, 1]";
	private static final Pattern PATTERN = Pattern.compile(EVENT_REGEX);
	
	public static boolean isEvent(String logLine) {
		Matcher matcher = PATTERN.matcher(logLine);
		return matcher.find();
	}
	
	//event-attributes 
	private int processID;
	private int treeIndex;
	private int jobID;
	private boolean load;
	private Date time;
	

	/**
	 * Constructor of event 
	 * @param logLine which has to match the Event.EVENT_REGEX
	 */
	public Event(String logLine) {
		super(logLine);
		setEventAttributes(logLine);
	}
	
	
	public Event(int processID, int treeIndex, int jobID, boolean load, Date time) {
		super(null);
		this.processID = processID;
		this.treeIndex = treeIndex;
		this.jobID = jobID;
		this.load = load;
		this.time = time;
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


	public Date getTime() {
		return time;
	}

}
