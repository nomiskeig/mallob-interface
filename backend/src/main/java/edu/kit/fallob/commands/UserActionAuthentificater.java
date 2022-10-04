package edu.kit.fallob.commands;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.UserDao;
import edu.kit.fallob.dataobjects.User;
import edu.kit.fallob.springConfig.FallobException;
import org.springframework.http.HttpStatus;

/**
 * This class provides methods which check the access permissions and the user type of a User.
 * 
 * @author Maik Sept
 * @version 1.0
 *
 */
public class UserActionAuthentificater {

	private static final String NOT_FOUND_ERROR = "Error, the user could not be found in the database.";
	
	private UserDao userDao;
	
	public UserActionAuthentificater(DaoFactory daoFactory) {
		userDao = daoFactory.getUserDao();
	}
	
	
	public boolean hasAbortAccess(String username, int jobID) throws FallobException {
		User user = userDao.getUserByUsername(username);

		if (user == null) {
			throw new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND_ERROR);
		}

		return user.hasAbortAccess(jobID);
	}
	
	
	public boolean hasInformationAccess(String username, int jobID) throws FallobException {
		User user = userDao.getUserByUsername(username);

		if (user == null) {
			throw new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND_ERROR);
		}

		return user.hasInformationAccess(jobID);
	}
	
	
	public boolean hasResultAccess(String username, int jobID) throws FallobException {
		User user = userDao.getUserByUsername(username);

		if (user == null) {
			throw new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND_ERROR);
		}

		return user.hasResultAccess(jobID);
	}
	
	
	public boolean hasDescriptionAccessViaJobID(String username, int jobID) throws FallobException {
		User user = userDao.getUserByUsername(username);

		if (user == null) {
			throw new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND_ERROR);
		}

		return user.hasDescriptionAccess(jobID);
	}
	
	
	public boolean isOwnerOfJob(String username, int jobID) throws FallobException {
		User user = userDao.getUserByUsername(username);

		if (user == null) {
			throw new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND_ERROR);
		}

		return user.isOwnerOfJob(jobID);
	}
	
	public boolean isAdmin(String username) throws FallobException {
		User user = userDao.getUserByUsername(username);

		if (user == null) {
			throw new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND_ERROR);
		}

		return user.isAdmin();
	}
	
	
	public boolean hasDescriptionAccessViaDescriptionID(String username, int descriptionID) throws FallobException {
		return userDao.getUsernameByDescriptionId(descriptionID).equals(username);
	}

}
