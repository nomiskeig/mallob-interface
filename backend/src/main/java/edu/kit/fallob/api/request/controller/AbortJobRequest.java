package edu.kit.fallob.api.request.controller;

import java.util.List;

public class AbortJobRequest {

    private int[] jobIds;

    public AbortJobRequest(){}

    public AbortJobRequest(int[] jobIds) {
        this.jobIds = jobIds;
    }

    public int[] getJobIds() {
        return jobIds;
    }

    public void setJobIds(int[] jobIds) {
        this.jobIds = jobIds;
    }
}
