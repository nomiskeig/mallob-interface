package edu.kit.fallob.configuration;

import java.time.LocalDateTime;
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
	private long jobStorageTime;
	private long eventStorageTime;
	private long warningStorageTime;
	private long maxDescriptionStorageSize;
	private float defaultJobPriority;
	private String defaultWallClockLimit;
	private String defaultContentMode;
	private String descriptionsbasePath;
	private String databaseBasePath;
	private String mallobBasePath;
	private String resultBasePath;
	private String dataBaseUsername;
	private String databasePassword;


	private LocalDateTime startTime;

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
	
	
	public long getJobStorageTime() {
		return jobStorageTime;
	}
    void setJobStorageTime(long jobStorageTime) {
		this.jobStorageTime = jobStorageTime;
	}
    
    
	public long getEventStorageTime() {
		return eventStorageTime;
	}
	void setEventStorageTime(long eventStorageTime) {
		this.eventStorageTime = eventStorageTime;
	}
	
	
	public long getWarningStorageTime() {
		return warningStorageTime;
	}
	void setWarningStorageTime(long warningStorageTime) {
		this.warningStorageTime = warningStorageTime;
	}
	
	
	public long getMaxDescriptionStorageSize() {
		return maxDescriptionStorageSize;
	}
	void setMaxDescriptionStorageSize(long maxDescriptionStorageSize) {
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

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}



	public String getDataBaseUsername() {
		return dataBaseUsername;
	}


	public void setDataBaseUsername(String dataBaseUsername) {
		this.dataBaseUsername = dataBaseUsername;
	}


	public String getDatabasePassword() {
		return databasePassword;
	}


	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}





}
