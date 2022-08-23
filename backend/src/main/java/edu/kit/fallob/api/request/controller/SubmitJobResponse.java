package edu.kit.fallob.api.request.controller;

public class SubmitJobResponse {
    private final int jobID;

    public SubmitJobResponse(int jobID) {
        this.jobID = jobID;
    }

    public int getJobID() {
        return jobID;
    }
}
