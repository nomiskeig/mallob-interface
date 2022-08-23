package edu.kit.fallob.api.request.controller;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A request class for json parsing
 */
public class AbortJobRequest {

    private int[] jobs;

    /**
     * Standard Constructor, used by Spring for json Parsing
     */
    public AbortJobRequest(){}

    /**
     * Constructor that takes jobIds (mainly for testing)
     * @param jobs job ids
     */
    public AbortJobRequest(int[] jobs) {
        this.jobs = jobs;
    }

    /**
     * Getter
     * @return job ids
     */
    public int[] getJobs() {
        return jobs;
    }

    /**
     * Setter
     * @param jobs job ids
     */
    public void setJobs(int[] jobs) {
        this.jobs = jobs;
    }
}
