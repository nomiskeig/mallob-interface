package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JobListener implements OutputLogLineListener {
	
	private final static String JOB_DONE_REGEX = "SOLUTION #%d";
	private final static String JOB_CANCELLED_REGEX = "Interrupt #%d";
    private static final String WALLCLOCKLIMIT_REGEX = "#%d WALLCLOCK TIMEOUT";
    private static final String CPULIMIT_REGEX  = "#%d CPU TIMEOUT";
	
	private int mallobID;
	private boolean jobHasFinished;
	private Pattern donePattern;
	private Pattern cancelledPattern;
	private Pattern wallclockPattern;
	private Pattern cpuPattern;
	
	
	
	public JobListener(int mallobID) {
		this.mallobID = mallobID;
		this.jobHasFinished = false;
		String formattedJobFinishedRegex = String.format(JOB_DONE_REGEX, mallobID);
		String formattedJobCancelledRegex = String.format(JOB_CANCELLED_REGEX, mallobID);
		String formattedWallclocklimitRegex = String.format(WALLCLOCKLIMIT_REGEX, mallobID);
		String formattedCPUlimitRegex = String.format(CPULIMIT_REGEX, mallobID);
		donePattern = Pattern.compile(formattedJobFinishedRegex);
		cancelledPattern = Pattern.compile(formattedJobCancelledRegex);
		wallclockPattern = Pattern.compile(formattedWallclocklimitRegex);
		cpuPattern = Pattern.compile(formattedCPUlimitRegex);
	}

	@Override
	public void processLine(String line) {
		Matcher doneMatcher = donePattern.matcher(line);
		Matcher cancelledMatcher = cancelledPattern.matcher(line);
		Matcher wallclockMatcher = wallclockPattern.matcher(line);
		Matcher cpuMatcher = cpuPattern.matcher(line);
		if (doneMatcher.find() || cancelledMatcher.find() || wallclockMatcher.find() || cpuMatcher.find()) {
			jobHasFinished = true;
			synchronized(this) {
				notify();
			}
		}

	}
	
	
	public synchronized void waitForJob() {
		while (!jobHasFinished) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
