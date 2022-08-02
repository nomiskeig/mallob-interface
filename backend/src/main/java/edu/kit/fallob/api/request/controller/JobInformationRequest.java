package edu.kit.fallob.api.request.controller;

import java.util.List;

public class JobInformationRequest {

    private int[] jobIds;

    public JobInformationRequest(){}

    public JobInformationRequest(int[] jobIds) {
        this.jobIds = jobIds;
    }

    public int[] getJobIds() {
        return jobIds;
    }

    public void setJobIds(int[] jobIds) {
        this.jobIds = jobIds;
    }
}
