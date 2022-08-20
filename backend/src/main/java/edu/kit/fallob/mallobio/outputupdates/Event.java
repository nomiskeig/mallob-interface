package edu.kit.fallob.mallobio.outputupdates;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
	

	public static final String EVENT_REGEX = "LOAD" + OutputUpdate.REGEX_SEPARATOR + "[0, 1]";
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
	private LocalDateTime time;
	

	/**
	 * Constructor of event 
	 * @param logLine which has to match the Event.EVENT_REGEX
	 */
	public Event(String logLine) {
		super(logLine);
		setEventAttributes(logLine);
	}
	
	
	public Event(int processID, int treeIndex, int jobID, boolean load, LocalDateTime time) {
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
		String[] splittedLogLine = logLine.split(REGEX_SEPARATOR);
		time = LocalDateTime.now(ZoneOffset.UTC);
		
		processID = Integer.parseInt(splittedLogLine[1]);
		
		String jobIDandTreeIndexInfo = splittedLogLine[4];
		int treeIndexBegin = jobIDandTreeIndexInfo.indexOf(':') + 1;
		int treeIndexEnd = jobIDandTreeIndexInfo.length() - 1;
		treeIndex = Integer.parseInt(jobIDandTreeIndexInfo.substring(treeIndexBegin, treeIndexEnd));
		
		int jobIDBegin = jobIDandTreeIndexInfo.indexOf('#') + 1;
		int jobIDEnd = treeIndexBegin - 1;
		jobID = Integer.parseInt(jobIDandTreeIndexInfo.substring(jobIDBegin, jobIDEnd));
		
		load = Integer.parseInt(splittedLogLine[3]) == 1;
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


	public LocalDateTime getTime() {
		return time;
	}

}
