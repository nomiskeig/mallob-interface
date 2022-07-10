package edu.kit.fallob.api.request.stream;

public class MallobStreamRequest {
    private int jobId;
    private String[] regex;

    public MallobStreamRequest() {

    }

    public MallobStreamRequest(int jobId, String[] regex) {
        this.jobId = jobId;
        this.regex = regex;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String[] getRegex() {
        return regex;
    }

    public void setRegex(String[] regex) {
        this.regex = regex;
    }
}
