package edu.kit.fallob.api.request.controller;

public class JobInformationRequest {

    private int[] jobs;

    public JobInformationRequest(){}

    public JobInformationRequest(int[] jobs) {
        this.jobs = jobs;
    }

    public int[] getJobs() {
        return jobs;
    }

    public void setJobs(int[] jobs) {
        this.jobs = jobs;
    }
}
