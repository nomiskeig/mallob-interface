package edu.kit.fallob.configuration;

import java.util.List;

/**
 * This class holds all important system parameters which have to be set when 
 * the whole program starts. Furthermore this class is a singleton.
 * 
 * 
 * @author Maik Sept
 * @version 1.0
 *
 */
public class FallobConfiguration {
	
	private static FallobConfiguration instance = null;
	
	private int amountProcesses;
	private int maxJobsTotal;
	private int maxJobsUser;
	private int[] clientProcesses;
	private int garbageCollectorInterval;
	private int jobStorageTime;
	private int eventStorageTime;
	private int warningStorageTime;
	private int maxDescriptionStorageSize;
	private float defaultJobPriority;
	private String defaultWallClockLimit;
	private String defaultContentMode;
	private String descriptionsbasePath;
	private String databaseBasePath;
	private String mallobBasePath;
	private String resultBasePath;

	private String startTime;
	
	private FallobConfiguration() {
		
	}
	
	
	/**
	 * This method returns the only instance of {@link FallobConfiguration}.
	 * 
	 * @return an instance of {@link FallobConfiguration}.
	 */
	public static FallobConfiguration getInstance() {
		if (instance == null) {
			instance = new FallobConfiguration();
		}
		return instance;
	}
	
	
	
	public int getAmountProcesses() {
		return amountProcesses;
	}
	void setAmountProcesses(int amountProcesses) {
		this.amountProcesses = amountProcesses;
	}
	
	
	public int getMaxJobsTotal() {
		return maxJobsTotal;
	}
	void setMaxJobsTotal(int maxJobsTotal) {
		this.maxJobsTotal = maxJobsTotal;
	}
	
	
	public int getMaxJobsUser() {
		return maxJobsUser;
	}
	void setMaxJobsUser(int maxJobsUser) {
		this.maxJobsUser = maxJobsUser;
	}
	
	
	public int[] getClientProcesses() {
		return clientProcesses;
	}
	void setClientProcesses(int[] clientProcesses) {
		this.clientProcesses = clientProcesses;
	}
	
	
	public int getGarbageCollectorInterval() {
		return garbageCollectorInterval;
	}
	void setGarbageCollectorInterval(int garbageCollectorInterval) {
		this.garbageCollectorInterval = garbageCollectorInterval;
	}
	
	
	public int getJobStorageTime() {
		return jobStorageTime;
	}
    void setJobStorageTime(int jobStorageTime) {
		this.jobStorageTime = jobStorageTime;
	}
    
    
	public int getEventStorageTime() {
		return eventStorageTime;
	}
	void setEventStorageTime(int eventStorageTime) {
		this.eventStorageTime = eventStorageTime;
	}
	
	
	public int getWarningStorageTime() {
		return warningStorageTime;
	}
	void setWarningStorageTime(int warningStorageTime) {
		this.warningStorageTime = warningStorageTime;
	}
	
	
	public int getMaxDescriptionStorageSize() {
		return maxDescriptionStorageSize;
	}
	void setMaxDescriptionStorageSize(int maxDescriptionStorageSize) {
		this.maxDescriptionStorageSize = maxDescriptionStorageSize;
	}
	
	
	public float getDefaultJobPriority() {
		return defaultJobPriority;
	}
	void setDefaultJobPriority(float defaultJobPriority) {
		this.defaultJobPriority = defaultJobPriority;
	}
	
	
	public String getDefaultWallClockLimit() {
		return defaultWallClockLimit;
	}
	void setDefaultWallClockLimit(String defaultWallClockLimit) {
		this.defaultWallClockLimit = defaultWallClockLimit;
	}
	
	
	public String getDefaultContentMode() {
		return defaultContentMode;
	}
	void setDefaultContentMode(String defaultContentMode) {
		this.defaultContentMode = defaultContentMode;
	}
	
	
	public String getDescriptionsbasePath() {
		return descriptionsbasePath;
	}
	void setDescriptionsbasePath(String descriptionsbasePath) {
		this.descriptionsbasePath = descriptionsbasePath;
	}
	
	
	public String getDatabaseBasePath() {
		return databaseBasePath;
	}
	void setDatabaseBasePath(String databaseBasePath) {
		this.databaseBasePath = databaseBasePath;
	}
	
	
	public String getMallobBasePath() {
		return mallobBasePath;
	}
	void setMallobBasePath(String mallobBasePath) {
		this.mallobBasePath = mallobBasePath;
	}
	
	
	public String getResultBasePath() {
		return resultBasePath;
	}
	void setResultBasePath(String resultBasePath) {
		this.resultBasePath = resultBasePath;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}



}
