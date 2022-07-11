package edu.kit.fallob.dataobjects;

public class JobInformation {
	
	private JobConfiguration jobConfiguration;
	private ResultMetaData rmd;
	private User user;
	private String submitTime;
	private JobStatus jobStatus;
	private int jobID;
	
	
	public JobInformation(JobConfiguration jobConfiguration, ResultMetaData rmd, 
			User user, String submitTime, JobStatus jobStatus, 
			int jobID) {
		this.setJobConfiguration(jobConfiguration);
		this.setResultMetaData(rmd);
		this.setUser(user);
		this.setSubmitTime(submitTime);
		this.setJobStatus(jobStatus);
		this.setJobID(jobID);
	}
	
	public JobConfiguration getJobConfiguration() {
		return jobConfiguration;
	}
	public void setJobConfiguration(JobConfiguration jobConfiguration) {
		this.jobConfiguration = jobConfiguration;
	}
	public ResultMetaData getResultMetaData() {
		return rmd;
	}
	public void setResultMetaData(ResultMetaData rmd) {
		this.rmd = rmd;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}
	public JobStatus getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}
	public int getJobID() {
		return jobID;
	}
	public void setJobID(int jobID) {
		this.jobID = jobID;
	}

}
