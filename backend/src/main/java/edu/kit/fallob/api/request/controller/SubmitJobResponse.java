package edu.kit.fallob.api.request.controller;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A response class for json parsing
 */
public class SubmitJobResponse {
    private final int jobID;

    public SubmitJobResponse(int jobID) {
        this.jobID = jobID;
    }

    public int getJobID() {
        return jobID;
    }
}
