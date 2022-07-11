package edu.kit.fallob.api.request.controller;

import java.util.List;

public class JobInformationRequest {

    private List<Integer> jobIds;

    public JobInformationRequest(){}

    public JobInformationRequest(List<Integer> jobIds) {
        this.jobIds = jobIds;
    }

    public List<Integer> getJobIds() {
        return jobIds;
    }

    public void setJobIds(List<Integer> jobIds) {
        this.jobIds = jobIds;
    }
}
