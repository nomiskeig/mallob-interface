package edu.kit.fallob.api.request.controller;


import edu.kit.fallob.dataobjects.*;

public class JobInformationProxy {

    private final JobConfiguration config;
    private final ResultMetaData resultData;
    private final String email;
    private final String user;
    private final String submitTime;
    private final JobStatus status;
    private final int jobID;

    public JobInformationProxy(JobInformation jobInformation) {
        this.config = jobInformation.getJobConfiguration();
        this.jobID = jobInformation.getJobID();
        this.status = jobInformation.getJobStatus();
        this.submitTime = jobInformation.getSubmitTime();
        this.resultData = jobInformation.getResultMetaData();
        this.email = jobInformation.getUser().getEmail();
        this.user = jobInformation.getUser().getUsername();
    }

    public JobConfiguration getConfig() {
        return config;
    }
    public int getJobID() {
        return jobID;
    }
    public String getEmail() {
        return email;
    }
    public String getUser() {
        return user;
    }
    public String getSubmitTime() {
        return submitTime;
    }
    public JobStatus getStatus() {
        return status;
    }
    public ResultMetaData resultData() {
        return resultData;
    }
}
