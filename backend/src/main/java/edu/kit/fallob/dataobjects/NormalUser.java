package edu.kit.fallob.dataobjects;

import java.util.List;

public class NormalUser extends User {
	
	private static final double NORMAL_USER_PRIORITY = 0.5;
	private static final String STRING_REPRESENTATION = "Normal User";

	public NormalUser(String username, String password, String email) {
		super(username, password, email);
		this.setAdmin(false);
		this.setPriority(NORMAL_USER_PRIORITY);
		this.setUserType(UserType.NORMAL_USER);
	}

	@Override
	public boolean hasAbortAccess(int jobID) {
		return isOwnerOfJob(jobID);
	}

	@Override
	public boolean hasResultAccess(int jobID) {
		return isOwnerOfJob(jobID);
	}

	@Override
	public boolean hasInformationAccess(int jobID) {
		return isOwnerOfJob(jobID);
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
