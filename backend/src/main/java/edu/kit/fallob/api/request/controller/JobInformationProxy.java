package edu.kit.fallob.api.request.controller;


import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobInformation;
import edu.kit.fallob.dataobjects.JobStatus;
import edu.kit.fallob.dataobjects.ResultMetaData;

public class JobInformationProxy {

    private JobInformation jobInformation;

    public JobInformationProxy(JobInformation jobInformation) {
        this.jobInformation = jobInformation;
    }

    public JobConfiguration getJobConfig() {
        return jobInformation.getJobConfiguration();
    }
    public int getJobId() {
        return jobInformation.getJobID();
    }
    public String getUserEmail() {
        return jobInformation.getUser().getEmail();
    }
    public String getUserName() {
        return jobInformation.getUser().getUsername();
    }
    public String getUserSubmitTime() {
        return jobInformation.getSubmitTime();
    }
    public JobStatus getUserJobStatus() {
        return jobInformation.getJobStatus();
    }
    public ResultMetaData getResultMetaData() {
        return jobInformation.getResultMetaData();
    }
}
