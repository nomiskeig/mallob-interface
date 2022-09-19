package edu.kit.fallob.dataobjects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultMetaData {
	
	private double parsingTime;
	private double processingTime;
	private double schedulingTime;
	private double totalTime;
    @JsonProperty("usedCpuSeconds")
	private double cpuSeconds;
    @JsonProperty("usedWallclockSeconds")
	private double wallclockSeconds;
	
	public ResultMetaData(double parsingTime, double processingTime, 
			double schedulingTime, double totalTime, 
			double cpuSeconds, double wallclockSeconds) {
		this.setParsingTime(parsingTime);
		this.setProcessingTime(processingTime);
		this.setSchedulingTime(schedulingTime);
		this.setTotalTime(totalTime);
		this.setCpuSeconds(cpuSeconds);
		this.setWallclockSeconds(wallclockSeconds);
	}
	
	public double getParsingTime() {
		return parsingTime;
	}
	public void setParsingTime(double parsingTime) {
		this.parsingTime = parsingTime;
	}
	public double getProcessingTime() {
		return processingTime;
	}
	public void setProcessingTime(double processingTime) {
		this.processingTime = processingTime;
	}
	public double getSchedulingTime() {
		return schedulingTime;
	}
	public void setSchedulingTime(double schedulingTime) {
		this.schedulingTime = schedulingTime;
	}
	public double getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}
	public double getCpuSeconds() {
		return cpuSeconds;
	}
	public void setCpuSeconds(double cpuSeconds) {
		this.cpuSeconds = cpuSeconds;
	}
	public double getWallclockSeconds() {
		return wallclockSeconds;
	}
	public void setWallclockSeconds(double wallclockSeconds) {
		this.wallclockSeconds = wallclockSeconds;
	}

}
