package edu.kit.fallob.api.request.controller;

import java.util.List;

public class JobInformationResponse {

    private List<JobInformationProxy> jobInformation;

    public JobInformationResponse(List<JobInformationProxy> jobInformation) {
        this.jobInformation = jobInformation;
    }

    public List<JobInformationProxy> getJobInformation() {
        return jobInformation;
    }
}
