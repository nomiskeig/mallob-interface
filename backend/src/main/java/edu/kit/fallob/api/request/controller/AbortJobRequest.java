package edu.kit.fallob.api.request.controller;

public class AbortJobRequest {

    private int[] jobs;

    public AbortJobRequest(){}

    public AbortJobRequest(int[] jobs) {
        this.jobs = jobs;
    }

    public int[] getJobs() {
        return jobs;
    }

    public void setJobs(int[] jobs) {
        this.jobs = jobs;
    }
}
