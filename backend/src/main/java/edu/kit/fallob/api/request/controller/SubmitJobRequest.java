package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.dataobjects.JobConfiguration;

import java.io.Serializable;
import java.util.List;

public class SubmitJobRequest implements Serializable {
    private List<String> jobDescription;
    private JobConfiguration jobConfiguration;
    private String url;

    public SubmitJobRequest(){}

    public SubmitJobRequest(List<String> jobDescription, JobConfiguration jobConfiguration) {
        this.jobDescription = jobDescription;
        this.jobConfiguration = jobConfiguration;
    }
    public SubmitJobRequest(JobConfiguration jobConfig) {
        this.jobConfiguration = jobConfig;
    }
    public SubmitJobRequest(JobConfiguration jobConfig, String url) {
        this.jobConfiguration = jobConfig;
        this.url = url;
    }

    public List<String> getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(List<String> jobDescription) {
        this.jobDescription = jobDescription;
    }

    public JobConfiguration getJobConfiguration() {
        return jobConfiguration;
    }

    public void setJobConfiguration(JobConfiguration jobConfiguration) {
        this.jobConfiguration = jobConfiguration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
