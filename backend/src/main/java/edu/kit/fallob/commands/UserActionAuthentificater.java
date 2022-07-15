package edu.kit.fallob.commands;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.database.UserDao;
import edu.kit.fallob.dataobjects.User;

/**
 * This class provides methods which check the access permissions and the user type of a User.
 * 
 * @author Maik Sept
 * @version 1.0
 *
 */
public class UserActionAuthentificater {
	
	private DaoFactory daoFactory;
	
	
	private User getUser(String username) {
		UserDao userDao = daoFactory.getUserDao();
		return userDao.getUserByUsername(username);
	}
	
	
	public boolean hasAbortAccess(String username, int jobID) {
		return getUser(username).hasAbortAccess(jobID);
	}
	
	
	public boolean hasInformationAccess(String username, int jobID) {
		return getUser(username).hasInformationAccess(jobID);
	}
	
	
	public boolean hasResultAccess(String username, int jobID) {
		return getUser(username).hasResultAccess(jobID);
	}
	
	
	public boolean hasDescriptionAccessViaJobID(String username, int jobID) {
		return getUser(username).hasDescriptionAccess(jobID);
	}
	
	
	public boolean isOwnerOfJob(String username, int jobID) {
		return getUser(username).isOwnerOfJob(jobID);
	}
	
	public boolean isAdmin(String username) {
		return getUser(username).isAdmin(); 
	}
	
	
	public boolean hasDescriptionAccessViaDescriptionID(String username, int descriptionID) {
		UserDao userDao = daoFactory.getUserDao();
		return userDao.getUsernameByDescriptionId(descriptionID).equals(username);
	}

}
