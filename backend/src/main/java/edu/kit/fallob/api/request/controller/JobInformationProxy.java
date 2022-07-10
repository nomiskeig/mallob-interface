package edu.kit.fallob.api.request.controller;

public class JobInformationProxy {

    private JobInformation jobInformation;

    public JobInformationProxy(JobInformation jobInformation) {
        this.jobInformation = jobInformation;
    }

    public JobConfiguration getJobConfig() {
        return null;
    }
    public int getJobId() {
        return 0;
    }
    public String getUserEmail() {
        return null;
    }
    public String getUserName() {
        return null;
    }
    public String getUserSubmitTime() {
        return null;
    }
    public String getUserJobStatus() {
        return null;
    }
    public ResultMetaData getResultMetaData() {
        return null;
    }
}
