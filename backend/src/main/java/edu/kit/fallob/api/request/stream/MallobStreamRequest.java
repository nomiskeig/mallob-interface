package edu.kit.fallob.api.request.stream;

/**
 * class that holds the data necessary for a custom log line stream
 * @author Valentin Schenk
 * @version 1.0
 */
public class MallobStreamRequest {
    private int jobId;
    private String[] regex;

    /**
     * standard constructor for the class
     */
    public MallobStreamRequest() {
    }

    /**
     * constructor for the class
     * @param jobId the id of the for which the log stream should be established
     * @param regex the regex pattern that defines for which log lines should be listened
     */
    public MallobStreamRequest(int jobId, String[] regex) {
        this.jobId = jobId;
        this.regex = regex;
    }

    /**
     * getter for the job id
     * @return the job id
     */
    public int getJobId() {
        return jobId;
    }

    /**
     * setter for the job id
     * @param jobId the job id that should be set
     */
    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    /**
     * getter for the regex pattern
     * @return the regex pattern
     */
    public String[] getRegex() {
        return regex;
    }

    /**
     * setter for the regex pattern
     * @param regex the regex pattern that should be set
     */
    public void setRegex(String[] regex) {
        this.regex = regex;
    }
}
