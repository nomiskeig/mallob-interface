package edu.kit.fallob.dataobjects;

import java.util.ArrayList;
import java.util.List;

public abstract class User {
	
	private static final double BASE_PRIORITY = 0.5;
	
	protected String username;
	protected String password;
	protected String email;
	protected boolean isAdmin;
	protected double priority;
	protected boolean isVerified;
	protected List<Integer> jobIDs;

	protected UserType userType;
	
	
	
	public User(String username, String password, 
			String email) {
		this.setUsername(username);
		this.setPassword(password);
		this.setEmail(email);
		this.setVerified(false);
		this.setJobIDs(new ArrayList<>());
	}
	
	
	public boolean isOwnerOfJob(int jobID) {
		for (Integer id : jobIDs) {
			if (jobID == id.intValue()) {
				return true;
			}
		}
		return false;
	}
	
	
	public abstract boolean hasAbortAccess(int jobID);
	
	
	public abstract boolean hasResultAccess(int jobID);
	
	
	public abstract boolean hasInformationAccess(int jobID);
	
	
	public abstract boolean hasDescriptionAccess(int jobID);
	
	
	public abstract String toString();
		
	
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public double getPriority() {
		return priority;
	}
	public void setPriority(double priority) {
		this.priority = priority;
	}
	public boolean isVerified() {
		return isVerified;
	}
	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}
	public List<Integer> getJobIDs() {
		return jobIDs;
	}
	public void setJobIDs(List<Integer> jobIDs) {
		this.jobIDs = jobIDs;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

}
