package edu.kit.fallob.api.request.controller;

import java.util.List;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A response class for json parsing
 */
public class AbortJobResponse {

    private final List<Integer> jobs;

    /**
     * Constructor that initializes the jobs
     * @param jobs job ids
     */
    public AbortJobResponse(List<Integer> jobs) {
        this.jobs = jobs;
    }

    /**
     * Getter
     * @return job ids
     */
    public List<Integer> getJobs() {
        return jobs;
    }
}
