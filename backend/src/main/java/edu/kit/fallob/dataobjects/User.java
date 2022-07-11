package edu.kit.fallob.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class User {
	
	private String username;
	private String password;
	private String email;
	private double priority;
	private boolean isVerified;
	private List<Integer> jobIDs;
	
	
	public User(String username, String password, 
			String email, double priority, 
			boolean isVerified, List<Integer> jobIDs) {
		this.setUsername(username);
		this.setPassword(password);
		this.setEmail(email);
		this.setPriority(priority);
		this.setVerified(isVerified);
		this.setJobIDs(jobIDs);
	}
	
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

}
