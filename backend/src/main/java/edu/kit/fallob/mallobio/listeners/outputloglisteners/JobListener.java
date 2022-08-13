package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JobListener implements OutputLogLineListener {
	
	private final static String JOB_FINISHED_REGEX = "SOLUTION #%d";
	
	private int mallobID;
	private boolean jobHasFinished;
	private Pattern pattern;
	
	
	
	public JobListener(int mallobID) {
		this.mallobID = mallobID;
		this.jobHasFinished = false;
		String formattedJobFinishedRegex = String.format(JOB_FINISHED_REGEX, mallobID);
		pattern = Pattern.compile(formattedJobFinishedRegex);
	}

	@Override
	public void processLine(String line) {
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
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
