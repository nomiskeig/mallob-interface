package edu.kit.fallob.api.request.controller;

import java.util.List;

public class AbortJobResponse {

    private final List<Integer> jobs;

    public List<Integer> getJobs() {
        return jobs;
    }

    public AbortJobResponse(List<Integer> jobs) {
        this.jobs = jobs;
    }

}
