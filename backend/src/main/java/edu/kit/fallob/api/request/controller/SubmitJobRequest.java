package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.dataobjects.JobConfiguration;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A request class for json parsing
 */
public class SubmitJobRequest {
    private List<String> description;

    private JobConfiguration jobConfiguration;
    private String url;

    /**
     * Standard constructor (for json parsing)
     * Initializes also a JobConfiguration object with default values that are required by mallob
     */
    public SubmitJobRequest(){
        jobConfiguration = new JobConfiguration(null, JobConfiguration.DOUBLE_NOT_SET, null);
    }

    /**
     * Constructor used for requests with inclusive description
     * @param description A list of Strings representing the JobDescription
     * @param jobConfiguration the given jobConfiguration object
     */
    public SubmitJobRequest(List<String> description, JobConfiguration jobConfiguration) {
        this.description = description;
        this.jobConfiguration = jobConfiguration;
    }

    /**
     * Constructor used for requests with exclusive description (jobConfig contains a descriptionId to the jobDescription)
     * @param jobConfig the given jobConfiguration object
     */
    public SubmitJobRequest(JobConfiguration jobConfig) {
        this.jobConfiguration = jobConfig;
    }

    /**
     * Constructor used for requests with description as url
     * @param jobConfig the given jobConfiguration object
     * @param url the given url to the jobDescription files
     */
    public SubmitJobRequest(JobConfiguration jobConfig, String url) {
        this.jobConfiguration = jobConfig;
        this.url = url;
    }

    /**
     * Getter
     * @return jobDescription
     */
    public List<String> getDescription() {
        return description;
    }

    /**
     * Setter
     * @param description given jobDescription
     */
    public void setDescription(List<String> description) {
        this.description = description;
    }

    /**
     * Getter
     * @return the jobConfiguration
     */
    public JobConfiguration getJobConfiguration() {
        return jobConfiguration;
    }

    /**
     * Getter
     * @return the jobName
     */
    public String getName() {
        return jobConfiguration.getName();
    }

    /**
     * Setter
     * @param name the jobName
     */
    public void setName(String name) {
        jobConfiguration.setName(name);
    }

    /**
     * Getter
     * @return the jobPriority
     */
    public double getPriority() {
        return jobConfiguration.getPriority();
    }

    /**
     * Setter
     * @param priority the jobPriority
     */
    public void setPriority(double priority) {
        jobConfiguration.setPriority(priority);
    }

    /**
     * Getter
     * @return the description Application (SAT, k-means...)
     */
    public String getApplication() {
        return jobConfiguration.getApplication();
    }

    /**
     * Setter
     * @param application the description Application (SAT, k-means...)
     */
    public void setApplication(String application) {
        this.jobConfiguration.setApplication(application);
    }

    /**
     * Getter
     * @return the maxDemand of threads from Mallob for this job
     */
    public int getMaxDemand() {
        return jobConfiguration.getMaxDemand();
    }

    /**
     * Setter
     * @param maxDemand the maxDemand of threads from Mallob for this job
     */
    public void setMaxDemand(int maxDemand) {
        jobConfiguration.setMaxDemand(maxDemand);
    }

    /**
     * Getter
     * @return the wallClockLimit for Mallob
     */
    public String getWallClockLimit() {
        return jobConfiguration.getWallClockLimit();
    }

    /**
     * Setter
     * @param wallClockLimit the wallClockLimit for Mallob
     */
    @JsonProperty("wallclockLimit")
    public void setWallClockLimit(String wallClockLimit) {
        jobConfiguration.setWallClockLimit(wallClockLimit);
    }

    /**
     * Getter
     * @return the cpuLimit for Mallob
     */
    public String getCpuLimit() {
        return jobConfiguration.getCpuLimit();
    }

    /**
     * Setter
     * @param cpuLimit the cpuLimit for Mallob
     */
    public void setCpuLimit(String cpuLimit) {
        jobConfiguration.setCpuLimit(cpuLimit);
    }

    /**
     * Getter
     * @return the arrival parameter
     */
    public String getArrival() {
        return jobConfiguration.getArrival();
    }

    /**
     * Setter
     * @param arrival the arrival parameter
     */
    public void setArrival(String arrival) {
        jobConfiguration.setArrival(arrival);
    }

    /**
     * Getter
     * @return the Dependencies as jobIds in case of an incremental job
     */
    public Integer[] getDependencies() {
        return jobConfiguration.getDependencies();
    }

    /**
     * Setter
     * @param dependencies2 the Dependencies as jobIds in case of an incremental job
     */
    public void setDependencies(Integer[] dependencies2) {
        jobConfiguration.setDependencies(dependencies2);
    }

    /**
     * Getter
     * @return true if job is incremental
     */
    public boolean isIncremental() {
        return jobConfiguration.isIncremental();
    }

    /**
     * Setter
     * @param incremental true if job is incremental
     */
    public void setIncremental(boolean incremental) {
        jobConfiguration.setIncremental(incremental);
    }

    /**
     * Getter
     * @return the precursor parameter
     */
    public int getPrecursor() {
        return jobConfiguration.getPrecursor();
    }

    /**
     * Setter
     * @param precursor the precursor parameter
     */
    public void setPrecursor(int precursor) {
        jobConfiguration.setPrecursor(precursor);
    }

    /**
     * Getter
     * @return the descriptionId in case of an exclusive description
     */
    public int getDescriptionID() {
        return jobConfiguration.getDescriptionID();
    }

    /**
     * Setter
     * @param decriptionID the descriptionId in case of an exclusive description
     */
    public void setDescriptionID(int decriptionID) {
        jobConfiguration.setDescriptionID(decriptionID);
    }

    /**
     * Getter
     * @return the additionlConfig parameter
     */
    @JsonProperty("additionalConfig") 
    @JsonDeserialize(using = KeepAsJsonDeserializer.class)
    public String getAdditionalParameter() {
        return jobConfiguration.getAdditionalParameter();
    }

    /**
     * Setter
     * @param additionalParameter the additionlConfig parameter
     */
    public void setAdditionalParameter(String additionalParameter) {
        jobConfiguration.setAdditionalParameter(additionalParameter);
    }

    /**
     * Getter
     * @return true if the job is to be interrupted (for incremental jobs)
     */
    public boolean isInterrupt() {
        return jobConfiguration.isInterrupt();
    }

    /**
     * Setter
     * @param interrupt true if the job is to be interrupted (for incremental jobs)
     */
    public void setInterrupt(boolean interrupt) {
        jobConfiguration.setInterrupt(interrupt);
    }

    /**
     * Getter
     * @return the contentMode of the description (text, raw...)
     */
    public String getContentMode() {
        return jobConfiguration.getContentMode();
    }

    /**
     * Setter
     * @param contentMode the contentMode of the description (text, raw...)
     */
    public void setContentMode(String contentMode) {
        jobConfiguration.setContentMode(contentMode);
    }

    /**
     * Getter
     * @return the precursor parameter as String
     */
    public String getPrecursorString() {
        return jobConfiguration.getPrecursorString();
    }

    /**
     * Setter
     * @param precursorString the precursor parameter as String
     */
    public void setPrecursorString(String precursorString) {
        jobConfiguration.setPrecursorString(precursorString);
    }

    /**
     * Getter
     * @return the assumptions parameter
     */
    public String getAssumptions() {
        return jobConfiguration.getAssumptions();
    }

    /**
     * Setter
     * @param assumptions the assumptions parameter
     */
    public void setAssumptions(String assumptions) {
        jobConfiguration.setAssumptions(assumptions);
    }

    /**
     * Getter
     * @return the done parameter for incremental jobs
     */
    public boolean isDone() {
        return jobConfiguration.isDone();
    }

    /**
     * Setter
     * @param done the done parameter for incremental jobs
     */
    public void setDone(boolean done) {
        jobConfiguration.setDone(done);
    }

    /**
     * Getter
     * @return the Literals as parameter
     */
    public int[] getLiterals() {
        return jobConfiguration.getLiterals();
    }

    /**
     * Setter
     * @param literals the Literals as parameter
     */
    public void setLiterals(int[] literals) {
        jobConfiguration.setLiterals(literals);
    }

    /**
     * Getter
     * @return the dependencies as Strings
     */
    public String[] getDependenciesStrings() {
        return jobConfiguration.getDependenciesStrings();
    }

    /**
     * Setter
     * @param dependenciesStrings the dependencies as Strings
     */
    public void setDependenciesStrings(String[] dependenciesStrings) {
        jobConfiguration.setDependenciesStrings(dependenciesStrings);
    }

    /**
     * Getter
     * @return the url with the jobDescription
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter
     * @param url the url with the jobDescription
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
