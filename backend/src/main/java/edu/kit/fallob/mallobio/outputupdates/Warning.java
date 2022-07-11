package edu.kit.fallob.mallobio.outputupdates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class Warning extends OutputUpdate {
	
	public static final String WARNING_REGEX = ""; //TODO
	private static final Pattern PATTERN = Pattern.compile(WARNING_REGEX);
	
	public static boolean isWarning(String logLine) {
		Matcher matcher = PATTERN.matcher(logLine);
		return matcher.find();
	}

	public Warning(String logLine) {
		super(logLine);
	}

}
