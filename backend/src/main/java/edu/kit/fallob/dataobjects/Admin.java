package edu.kit.fallob.dataobjects;

import java.util.List;

public class Admin extends User {
	
	private static final double ADMIN_PRIORITY = 0.7;
	private static final String STRING_REPRESENTATION = "Admin";

	public Admin(String username, String password, String email) {
		super(username, password, email);
		this.setAdmin(true);
		this.setPriority(ADMIN_PRIORITY);
		
	}

	@Override
	public boolean hasAbortAccess(int jobID) {
		return true;
	}

	@Override
	public boolean hasResultAccess(int jobID) {
		return isOwnerOfJob(jobID);
	}

	@Override
	public boolean hasInformationAccess(int jobID) {
		return true;
	}

	@Override
	public boolean hasDescriptionAccess(int jobID) {
		return isOwnerOfJob(jobID);
	}

	@Override
	public String toString() {
		return STRING_REPRESENTATION;
	}
	
	
	

}
