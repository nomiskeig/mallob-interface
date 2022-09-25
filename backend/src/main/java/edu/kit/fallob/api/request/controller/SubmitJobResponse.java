package edu.kit.fallob.api.request.controller;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A response class for json parsing
 */
public class SubmitJobResponse {
    private final int jobID;

    /**
     * Constructor that sets the jobID
     * @param jobID the id for the submitted job
     */
    public SubmitJobResponse(int jobID) {
        this.jobID = jobID;
    }

    /**
     * Getter
     * @return the id for the submitted job
     */
    public int getJobID() {
        return jobID;
    }
}
