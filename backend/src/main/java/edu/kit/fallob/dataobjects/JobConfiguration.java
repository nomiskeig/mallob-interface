package edu.kit.fallob.dataobjects;

import java.util.List;

public class JobConfiguration {

	private String name;
	private double priority;
	private String application;
	private int maxDemand;
	private String wallClockLimit;
	private String cpuLimit;
	private String arrival;
	private int[] dependencies;
	private String[] dependenciesStrings;
	private String contentMode;
	private boolean interrupt;
	private boolean incremental;
	private int[] literals;
	private int precursor;
	private String precursorString;
	private String assumptions;
	private boolean done;
	private int descriptionID;
	private String additionalParameter;
	
	
	public JobConfiguration(String name, double priority, 
			String application, int maxDemand, 
			String wallClockLimit, String cpuLimit, 
			String arrival, int[] dependencies,
			String contentMode, boolean interrupt, 
			boolean incremental, int[] literals, 
			int precursor, String assumptions, 
			boolean done, int decriptionID, String additionalParameter) {
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
	public String getWallClockLimit() {
		return wallClockLimit;
	}
	public void setWallClockLimit(String wallClockLimit) {
		this.wallClockLimit = wallClockLimit;
	}
	public String getCpuLimit() {
		return cpuLimit;
	}
	public void setCpuLimit(String cpuLimit) {
		this.cpuLimit = cpuLimit;
	}
	public String getArrival() {
		return arrival;
	}
	public void setArrival(String arrival) {
		this.arrival = arrival;
	}
	public int[] getDependencies() {
		return dependencies;
	}
	public void setDependencies(int[] dependencies) {
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
	public String getAdditionalParameter() {
		return additionalParameter;
	}
	public void setAdditionalParameter(String additionalParameter) {
		this.additionalParameter = additionalParameter;
	}

	public boolean isInterrupt() {
		return interrupt;
	}

	public void setInterrupt(boolean interrupt) {
		this.interrupt = interrupt;
	}

	public String getContentMode() {
		return contentMode;
	}

	public void setContentMode(String contentMode) {
		this.contentMode = contentMode;
	}

	public String getPrecursorString() {
		return precursorString;
	}

	public void setPrecursorString(String precursorString) {
		this.precursorString = precursorString;
	}

	public String getAssumptions() {
		return assumptions;
	}

	public void setAssumptions(String assumptions) {
		this.assumptions = assumptions;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public int[] getLiterals() {
		return literals;
	}

	public void setLiterals(int[] literals) {
		this.literals = literals;
	}

	public String[] getDependenciesStrings() {
		return dependenciesStrings;
	}

	public void setDependenciesStrings(String[] dependenciesStrings) {
		this.dependenciesStrings = dependenciesStrings;
	}
	
}	