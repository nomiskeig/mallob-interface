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
public class StatusUpdate extends OutputUpdate {

    private static final String STATUSUPDTATE_CANCELLED_REGEX = "Interrupt #[0-9]+";
    private static final String STATUSUPDTATE_DONE_REGEX = "SOLUTION #[0-9]+";
    private static final String STATUSUPDATE_WALLCLOCKLIMIT_REGEX = "#[0-9]+ WALLCLOCK TIMEOUT";
    private static final String STATUSUPDATE_CPULIMIT_REGEX  = "#[0-9]+ CPU TIMEOUT";
    public static final String STATUSUPDATE_REGEX = STATUSUPDTATE_DONE_REGEX + "|" + STATUSUPDTATE_CANCELLED_REGEX + "|"
            + STATUSUPDATE_WALLCLOCKLIMIT_REGEX + "|" + STATUSUPDATE_CPULIMIT_REGEX;

    private static final Pattern STATUSUPDATE_PATTERN = Pattern.compile(STATUSUPDATE_REGEX);
    private static final Pattern DONE_PATTERN = Pattern.compile(STATUSUPDTATE_DONE_REGEX);
    private static final Pattern CANCELLED_PATTERN = Pattern.compile(STATUSUPDTATE_CANCELLED_REGEX);
    private static final Pattern WALLCLOCK_PATTERN = Pattern.compile(STATUSUPDATE_WALLCLOCKLIMIT_REGEX);
    private static final Pattern CPU_PATTERN = Pattern.compile(STATUSUPDATE_CPULIMIT_REGEX);

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
        Matcher wallclockMatcher = WALLCLOCK_PATTERN.matcher(logLine);
        Matcher cpuMatcher = CPU_PATTERN.matcher(logLine);
        if (wallclockMatcher.find() || cpuMatcher.find()) {
            String jobIDString = splittedLogLine[2];
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
