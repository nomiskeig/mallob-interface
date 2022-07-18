package edu.kit.fallob.api.request.controller;

import java.util.List;

public class JobDescriptionResponse {

    private final List<String> jobDescription;

    public JobDescriptionResponse(List<String> jobDescription) {
        this.jobDescription = jobDescription;
    }

    public List<String> getJobDescription() {
        return jobDescription;
    }
}
