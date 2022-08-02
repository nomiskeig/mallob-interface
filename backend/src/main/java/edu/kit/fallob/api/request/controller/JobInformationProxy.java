package edu.kit.fallob.api.request.controller;


import edu.kit.fallob.dataobjects.*;

public class JobInformationProxy {

    private final JobConfiguration configuration;
    private final ResultMetaData rmd;
    private final String email;
    private final String username;
    private final String submitTime;
    private final JobStatus jobStatus;
    private final int jobID;

    public JobInformationProxy(JobInformation jobInformation) {
        this.configuration = jobInformation.getJobConfiguration();
        this.jobID = jobInformation.getJobID();
        this.jobStatus = jobInformation.getJobStatus();
        this.submitTime = jobInformation.getSubmitTime();
        this.rmd = jobInformation.getResultMetaData();
        this.email = jobInformation.getUser().getEmail();
        this.username = jobInformation.getUser().getUsername();
    }

    public JobConfiguration getConfiguration() {
        return configuration;
    }
    public int getId() {
        return jobID;
    }
    public String getEmail() {
        return email;
    }
    public String getUsername() {
        return username;
    }
    public String getSubmitTime() {
        return submitTime;
    }
    public JobStatus getJobStatus() {
        return jobStatus;
    }
    public ResultMetaData getResultMetaData() {
        return rmd;
    }
}
