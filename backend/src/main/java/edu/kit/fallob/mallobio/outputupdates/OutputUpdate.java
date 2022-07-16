package edu.kit.fallob.mallobio.outputupdates;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class OutputUpdate {
	
	public static final String TIME_REGEX = "";
	public static final String RANK_REGEX = "";
	
	public static final String REGEX_SEPARATOR = " ";

	protected String logLine;
	
	/**
	 * 
	 * @param logLine which creates this OuptutUpdate 
	 */
	public OutputUpdate(String logLine) {
		this.logLine = logLine;
	}
	
	/**
	 * Get the log line which triggered the creation of this OutputUpdate 
	 * @return null, if no log-line was responsible for creation, log-Line else
	 */
	public String getLogLine() {
		return logLine;
	}
	
	
	
	
}
