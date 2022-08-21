package edu.kit.fallob.dataobjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class JobConfiguration {
	
	public static final Object OBJECT_NOT_SET = null;
	
	public static final int INT_NOT_SET = - Integer.MAX_VALUE;
	public static final double DOUBLE_NOT_SET = -Double.MAX_VALUE;

	
	public static final boolean BOOL_DEFAULT = false;

	private String name;
	private double priority;
	private String application;
    @JsonInclude(value = Include.CUSTOM, valueFilter = JobConfigurationIntFilter.class)
	private int maxDemand;
    @JsonInclude(Include.NON_NULL)
	private String wallClockLimit;
    @JsonInclude(Include.NON_NULL)
	private String cpuLimit;
	private double arrival;
    @JsonInclude(value = Include.CUSTOM, valueFilter = JobConfigurationDependencyFilter.class)
	private Integer[] dependencies;
    @JsonInclude(value = Include.CUSTOM, valueFilter= JobConfigurationNeverFilter.class)
	private String[] dependenciesStrings;
    @JsonInclude(Include.NON_EMPTY)
	private String contentMode;
	private boolean interrupt;
	private boolean incremental;
    @JsonInclude(Include.NON_EMPTY)
	private int[] literals;
    @JsonInclude(value = Include.CUSTOM, valueFilter = JobConfigurationIntFilter.class)
	private int precursor;
    @JsonInclude(value = Include.CUSTOM, valueFilter = JobConfigurationNeverFilter.class)
	private String precursorString;
    @JsonInclude(Include.NON_EMPTY)
	private String assumptions;
	private boolean done;
    @JsonInclude(value = Include.CUSTOM, valueFilter = JobConfigurationNeverFilter.class)
	private int descriptionID;
    @JsonInclude(Include.NON_NULL)
	private String additionalParameter;

	public JobConfiguration(String name, double priority,
							String application)
	{
		this.setName(name);
		this.setPriority(priority);
		this.setApplication(application);

		this.setMaxDemand(INT_NOT_SET);
		this.setPrecursor(INT_NOT_SET);
		this.setArrival(DOUBLE_NOT_SET);

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
	public double getArrival() {
		return arrival;
	}
	public void setArrival(double arrival) {
		this.arrival = arrival;
	}
	
	public Integer[] getDependencies() {
		return dependencies;
	}
	public void setDependencies(Integer[] dependencies2) {
		this.dependencies = dependencies2;
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
