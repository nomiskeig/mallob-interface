package edu.kit.fallob.api.request.controller;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import edu.kit.fallob.dataobjects.*;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A proxy for the JobInformation class that contains only the required variables (for json parsing)
 */
public class JobInformationProxy {

    private final JobConfiguration config;
    @JsonInclude(Include.NON_NULL)
    private final ResultMetaData resultData;
    private final String email;
    private final String user;
    private final String submitTime;
    private final JobStatus status;
    private final int jobID;

    /**
     * Constructor that takes a jobInformation and parses the needed attributes
     * @param jobInformation the given jobInformation object
     */
    public JobInformationProxy(JobInformation jobInformation) {
        this.config = jobInformation.getJobConfiguration();
        this.jobID = jobInformation.getJobID();
        this.status = jobInformation.getJobStatus();
        this.submitTime = jobInformation.getSubmitTime();
        this.resultData = jobInformation.getResultMetaData();
        this.email = jobInformation.getUser().getEmail();
        this.user = jobInformation.getUser().getUsername();
    }

    /**
     * Getter
     * @return the jobConfiguration
     */
    public JobConfiguration getConfig() {
        return config;
    }

    /**
     * Getter
     * @return the jobId
     */
    public int getJobID() {
        return jobID;
    }

    /**
     * Getter
     * @return the user email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Getter
     * @return the username
     */
    public String getUser() {
        return user;
    }

    /**
     * Getter
     * @return the time of submission of the job
     */
    public String getSubmitTime() {
        return submitTime;
    }

    /**
     * Getter
     * @return the job status
     */
    public JobStatus getStatus() {
        return status;
    }

    /**
     * Getter
     * @return additional information to the job solution
     */
    public ResultMetaData getResultData() {
        return resultData;
    }
}
