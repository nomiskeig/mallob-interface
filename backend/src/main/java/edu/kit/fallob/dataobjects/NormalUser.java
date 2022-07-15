package edu.kit.fallob.dataobjects;

import java.util.List;

public class NormalUser extends User {

	public NormalUser(String username, String password, String email, boolean isAdmin, double priority,
			boolean isVerified, List<Integer> jobIDs) {
		super(username, password, email, isAdmin, priority, isVerified, jobIDs);
		
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

}
