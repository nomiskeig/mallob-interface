package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JobListener implements OutputLogLineListener {
	
	private final static String JOB_DONE_REGEX = "SOLUTION #%d";
	private final static String JOB_CANCELLED_REGEX = "Interrupt #%d";
	
	private int mallobID;
	private boolean jobHasFinished;
	private Pattern donePattern;
	private Pattern cancelledPattern;
	
	
	
	public JobListener(int mallobID) {
		this.mallobID = mallobID;
		this.jobHasFinished = false;
		String formattedJobFinishedRegex = String.format(JOB_DONE_REGEX, mallobID);
		String formattedJobCancelledRegex = String.format(JOB_CANCELLED_REGEX, mallobID);
		donePattern = Pattern.compile(formattedJobFinishedRegex);
		cancelledPattern = Pattern.compile(formattedJobCancelledRegex);
	}

	@Override
	public void processLine(String line) {
		Matcher doneMatcher = donePattern.matcher(line);
		Matcher cancelledMatcher = cancelledPattern.matcher(line);
		if (doneMatcher.find() || cancelledMatcher.find()) {
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
