package edu.kit.fallob.api.request.controller;

import java.util.List;

public class AbortJobResponse {

    private List<Integer> jobIds;

    public List<Integer> getJobIds() {
        return jobIds;
    }

    public AbortJobResponse(List<Integer> jobIds) {
        this.jobIds = jobIds;
    }

}
