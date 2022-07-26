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
	
	private UserDao userDao;
	
	public UserActionAuthentificater(DaoFactory daoFactory) {
		userDao = daoFactory.getUserDao();
	}
	
	
	public boolean hasAbortAccess(String username, int jobID) {
		return userDao.getUserByUsername(username).hasAbortAccess(jobID);
	}
	
	
	public boolean hasInformationAccess(String username, int jobID) {
		return userDao.getUserByUsername(username).hasInformationAccess(jobID);
	}
	
	
	public boolean hasResultAccess(String username, int jobID) {
		return userDao.getUserByUsername(username).hasResultAccess(jobID);
	}
	
	
	public boolean hasDescriptionAccessViaJobID(String username, int jobID) {
		return userDao.getUserByUsername(username).hasDescriptionAccess(jobID);
	}
	
	
	public boolean isOwnerOfJob(String username, int jobID) {
		return userDao.getUserByUsername(username).isOwnerOfJob(jobID);
	}
	
	public boolean isAdmin(String username) {
		return userDao.getUserByUsername(username).isAdmin(); 
	}
	
	
	public boolean hasDescriptionAccessViaDescriptionID(String username, int descriptionID) {
		return userDao.getUsernameByDescriptionId(descriptionID).equals(username);
	}

}
