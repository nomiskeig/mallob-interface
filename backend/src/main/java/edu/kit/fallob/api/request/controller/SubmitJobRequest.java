package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.dataobjects.JobConfiguration;

import java.io.Serializable;
import java.util.List;

public class SubmitJobRequest implements Serializable {
    private List<String> description;

    private JobConfiguration jobConfiguration;
    private String url;

    public SubmitJobRequest(){
        jobConfiguration = new JobConfiguration(null, JobConfiguration.DOUBLE_NOT_SET, null);
    }

    public SubmitJobRequest(List<String> description, JobConfiguration jobConfiguration) {
        this.description = description;
        this.jobConfiguration = jobConfiguration;
    }

    public SubmitJobRequest(JobConfiguration jobConfig) {
        this.jobConfiguration = jobConfig;
    }
    public SubmitJobRequest(JobConfiguration jobConfig, String url) {
        this.jobConfiguration = jobConfig;
        this.url = url;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public JobConfiguration getJobConfiguration() {
        return jobConfiguration;
    }

    public String getName() {
        return jobConfiguration.getName();
    }
    public void setName(String name) {
        jobConfiguration.setName(name);
    }
    public double getPriority() {
        return jobConfiguration.getPriority();
    }
    public void setPriority(double priority) {
        jobConfiguration.setPriority(priority);
    }
    public String getApplication() {
        return jobConfiguration.getApplication();
    }
    public void setApplication(String application) {
        this.jobConfiguration.setApplication(application);
    }
    public int getMaxDemand() {
        return jobConfiguration.getMaxDemand();
    }
    public void setMaxDemand(int maxDemand) {
        jobConfiguration.setMaxDemand(maxDemand);
    }
    public String getWallClockLimit() {
        return jobConfiguration.getWallClockLimit();
    }
    public void setWallClockLimit(String wallClockLimit) {
        jobConfiguration.setWallClockLimit(wallClockLimit);
    }
    public String getCpuLimit() {
        return jobConfiguration.getCpuLimit();
    }
    public void setCpuLimit(String cpuLimit) {
        jobConfiguration.setCpuLimit(cpuLimit);
    }
    public String getArrival() {
        return jobConfiguration.getArrival();
    }
    public void setArrival(String arrival) {
        jobConfiguration.setArrival(arrival);
    }

    public Integer[] getDependencies() {
        return jobConfiguration.getDependencies();
    }
    public void setDependencies(Integer[] dependencies2) {
        jobConfiguration.setDependencies(dependencies2);
    }
    public boolean isIncremental() {
        return jobConfiguration.isIncremental();
    }
    public void setIncremental(boolean incremental) {
        jobConfiguration.setIncremental(incremental);
    }
    public int getPrecursor() {
        return jobConfiguration.getPrecursor();
    }
    public void setPrecursor(int precursor) {
        jobConfiguration.setPrecursor(precursor);
    }
    public int getDescriptionID() {
        return jobConfiguration.getDescriptionID();
    }
    public void setDescriptionID(int decriptionID) {
        jobConfiguration.setDescriptionID(decriptionID);
    }
    public String getAdditionalParameter() {
        return jobConfiguration.getAdditionalParameter();
    }
    public void setAdditionalParameter(String additionalParameter) {
        jobConfiguration.setAdditionalParameter(additionalParameter);
    }

    public boolean isInterrupt() {
        return jobConfiguration.isInterrupt();
    }

    public void setInterrupt(boolean interrupt) {
        jobConfiguration.setInterrupt(interrupt);
    }

    public String getContentMode() {
        return jobConfiguration.getContentMode();
    }

    public void setContentMode(String contentMode) {
        jobConfiguration.setContentMode(contentMode);
    }

    public String getPrecursorString() {
        return jobConfiguration.getPrecursorString();
    }

    public void setPrecursorString(String precursorString) {
        jobConfiguration.setPrecursorString(precursorString);
    }

    public String getAssumptions() {
        return jobConfiguration.getAssumptions();
    }

    public void setAssumptions(String assumptions) {
        jobConfiguration.setAssumptions(assumptions);
    }

    public boolean isDone() {
        return jobConfiguration.isDone();
    }

    public void setDone(boolean done) {
        jobConfiguration.setDone(done);
    }

    public int[] getLiterals() {
        return jobConfiguration.getLiterals();
    }

    public void setLiterals(int[] literals) {
        jobConfiguration.setLiterals(literals);
    }

    public String[] getDependenciesStrings() {
        return jobConfiguration.getDependenciesStrings();
    }

    public void setDependenciesStrings(String[] dependenciesStrings) {
        jobConfiguration.setDependenciesStrings(dependenciesStrings);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
