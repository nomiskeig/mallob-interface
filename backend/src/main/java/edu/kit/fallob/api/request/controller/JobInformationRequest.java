package edu.kit.fallob.api.request.controller;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A request class for json parsing
 */
public class JobInformationRequest {

    private int[] jobs;

    /**
     * Standard constructor (for json parsing)
     */
    public JobInformationRequest(){}

    /**
     * Constructor that initializes the jobs
     * @param jobs jobIds
     */
    public JobInformationRequest(int[] jobs) {
        this.jobs = jobs;
    }

    /**
     * Getter
     * @return jobIds
     */
    public int[] getJobs() {
        return jobs;
    }

    /**
     * Setter
     * @param jobs jobIds
     */
    public void setJobs(int[] jobs) {
        this.jobs = jobs;
    }
}
