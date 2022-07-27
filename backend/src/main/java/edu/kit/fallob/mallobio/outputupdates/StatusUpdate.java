package edu.kit.fallob.mallobio.outputupdates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class StatusUpdate extends OutputUpdate  {
	
	public static final String STATUSUPDATE_REGEX = ""; //TODO
	private static final Pattern PATTERN = Pattern.compile(STATUSUPDATE_REGEX);
	
	public static boolean isJobStatus(String logLine) {
		Matcher matcher = PATTERN.matcher(logLine);
		return matcher.find();
	}

	public StatusUpdate(String logLine) {
		super(logLine);
		// TODO Auto-generated constructor stub
	}

}
