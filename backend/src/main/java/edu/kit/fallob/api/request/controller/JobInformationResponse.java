package edu.kit.fallob.api.request.controller;

import java.util.List;

public class JobInformationResponse {

    private final List<JobInformationProxy> information;

    public JobInformationResponse(List<JobInformationProxy> information) {
        this.information = information;
    }

    public List<JobInformationProxy> getInformation() {
        return information;
    }
}
