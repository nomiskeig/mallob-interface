package edu.kit.fallob.mallobio.outputupdates;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class Warning extends OutputUpdate {
	
	public static final String WARNING_REGEX = "WARN";
	private static final Pattern PATTERN = Pattern.compile(WARNING_REGEX);

	private final LocalDateTime time;
	private final String message;
	
	public static boolean isWarning(String logLine) {
		Matcher matcher = PATTERN.matcher(logLine);
		return matcher.find();
	}

	public Warning(String logLine) {
		super(logLine);
		int warnIndex = logLine.indexOf(WARNING_REGEX);
		this.message = logLine.substring(warnIndex - 3);
		this.time = LocalDateTime.now(ZoneOffset.UTC);
	}
	
	public Warning(String message, LocalDateTime time) {
		super(message);
		this.message = message;
		this.time = time;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public String getMessage() {
		return message;
	}
}
