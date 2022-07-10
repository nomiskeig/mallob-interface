package edu.kit.fallob.api.request.controller;

import java.util.List;

public class AbortJobRequest {

    private List<Integer> jobIds;

    public AbortJobRequest(){}

    public AbortJobRequest(List<Integer> jobIds) {
        this.jobIds = jobIds;
    }

    public List<Integer> getJobIds() {
        return jobIds;
    }
}
