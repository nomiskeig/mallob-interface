package edu.kit.fallob.api.request.controller;

public class SubmitJobResponse {
    private final int jobId;

    public SubmitJobResponse(int jobId) {
        this.jobId = jobId;
    }

    public int getJobId() {
        return jobId;
    }
}
