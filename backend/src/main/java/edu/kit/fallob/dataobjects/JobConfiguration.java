package edu.kit.fallob.dataobjects;

import java.util.List;

public class JobConfiguration {
	

	private String name;
	private double priority;
	private String application;
	private int maxDemand;
	private double wallClockLimit;
	private double cpuLimit;
	private double arrival;
	private List<Integer> dependencies;
	private boolean incremental;
	private int precursor;
	private int descriptionID;
	private List<String> additionalParameter;
	
	public JobConfiguration(String name, double priority, 
			String application, int maxDemand, 
			double wallClockLimit, double cpuLimit, 
			double arrival, List<Integer> dependencies, 
			boolean incremental, int precursor, 
			int decriptionID, List<String> additionalParameter) 
	{
		this.setName(name);
		this.setPriority(priority);
		this.setApplication(application);
		this.setMaxDemand(maxDemand);
	}
	
	/*
	public JobConfiguration(String name, double priority, 
			String application, int maxDemand, 
			double wallClockLimit, double cpuLimit, 
			double arrival, List<Integer> dependencies, 
			boolean incremental, int precursor, 
			int decriptionID, String additionalParameter) {
		this.setName(name);
		this.setPriority(priority);
		this.setApplication(application);
		this.setMaxDemand(maxDemand);
		this.setWallClockLimit(wallClockLimit);
		this.setCpuLimit(cpuLimit);
		this.setArrival(arrival);
		this.setDependencies(dependencies);
		this.setIncremental(incremental);
		this.setPrecursor(precursor);
		this.setDescriptionID(decriptionID);
		this.setAdditionalParameter(additionalParameter);
	}
	*/
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPriority() {
		return priority;
	}
	public void setPriority(double priority) {
		this.priority = priority;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public int getMaxDemand() {
		return maxDemand;
	}
	public void setMaxDemand(int maxDemand) {
		this.maxDemand = maxDemand;
	}
	public double getWallClockLimit() {
		return wallClockLimit;
	}
	public void setWallClockLimit(double wallClockLimit) {
		this.wallClockLimit = wallClockLimit;
	}
	public double getCpuLimit() {
		return cpuLimit;
	}
	public void setCpuLimit(double cpuLimit) {
		this.cpuLimit = cpuLimit;
	}
	public double getArrival() {
		return arrival;
	}
	public void setArrival(double arrival) {
		this.arrival = arrival;
	}
	public List<Integer> getDependencies() {
		return dependencies;
	}
	public void setDependencies(List<Integer> dependencies) {
		this.dependencies = dependencies;
	}
	public boolean isIncremental() {
		return incremental;
	}
	public void setIncremental(boolean incremental) {
		this.incremental = incremental;
	}
	public int getPrecursor() {
		return precursor;
	}
	public void setPrecursor(int precursor) {
		this.precursor = precursor;
	}
	public int getDescriptionID() {
		return descriptionID;
	}
	public void setDescriptionID(int decriptionID) {
		this.descriptionID = decriptionID;
	}
	public List<String> getAdditionalParameter() {
		return additionalParameter;
	}
	public void setAdditionalParameter(List<String> additionalParameter) {
		this.additionalParameter = additionalParameter;
	}
}	