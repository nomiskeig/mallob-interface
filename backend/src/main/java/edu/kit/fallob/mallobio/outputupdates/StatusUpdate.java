package edu.kit.fallob.mallobio.outputupdates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.kit.fallob.dataobjects.JobStatus;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class StatusUpdate extends OutputUpdate  {
	

	
	private static final String STATUSUPDTATE_CANCELLED_REGEX = "Interrupt #[0-9]+";
	private static final String STATUSUPDTATE_DONE_REGEX = "SOLUTION #[0-9]+";
	public static final String STATUSUPDATE_REGEX = STATUSUPDTATE_DONE_REGEX + "|" + STATUSUPDTATE_CANCELLED_REGEX;

	
	private static final Pattern STATUSUPDATE_PATTERN = Pattern.compile(STATUSUPDATE_REGEX);
	private static final Pattern DONE_PATTERN = Pattern.compile(STATUSUPDTATE_DONE_REGEX);
	private static final Pattern CANCELLED_PATTERN = Pattern.compile(STATUSUPDTATE_CANCELLED_REGEX);

	
	private int jobID;
	private JobStatus jobStatus;
	
	public static boolean isJobStatus(String logLine) {
		Matcher matcher = STATUSUPDATE_PATTERN.matcher(logLine);
		return matcher.find();
	}

	public StatusUpdate(String logLine) {
		super(logLine);
		String[] splittedLogLine = logLine.split(REGEX_SEPARATOR);
		Matcher doneMatcher = DONE_PATTERN.matcher(logLine);
		if (doneMatcher.find()) {
			String jobIDString = splittedLogLine[3];
			jobID = Integer.parseInt(jobIDString.substring(1));
			jobStatus = JobStatus.DONE;
		}
		Matcher cancelledMatcher = CANCELLED_PATTERN.matcher(logLine);
		if (cancelledMatcher.find()) {
			String jobIDString = splittedLogLine[3];
			jobID = Integer.parseInt(jobIDString.substring(1));
			jobStatus = JobStatus.CANCELLED;
		}

	}
	
	
	public int getJobID() {
		return jobID;
	}
	
	public JobStatus getJobStatus() {
		return jobStatus;
	}

}
