package edu.kit.fallob.database;

import edu.kit.fallob.dataobjects.User;

/**
 * An interface that represents a data access object for users
 * It is responsible for storing all the user data
 * @author Valentin Schenk
 * @version 1.0
 */
public interface UserDao {

    /**
     * saves a user in the database
     * @param user the user object that should be saved
     */
    public void save(User user);

    /**
     * removes a user from the database
     * @param username the name of the user that should be removed
     */
    public void remove(String username);

    /**
     * getter for a user that is identified by the username
     * @param username the name of the user that should be returned
     * @return the suer object that contains the data
     */
    public User getUserByUsername(String username);

    /**
     * returns the name of the user that submitted the job with the given id
     * @param jobId the id of the job for which the username should be returned
     * @return the name of the user that submitted the job with the id jobId
     */
    public String getUsernameByJobId(int jobId);

    /**
     * returns the name of the user that submitted the job-description with the given id
     * @param descriptionId the id of the job-description for which the username should be returned
     * @return the name of the user that submitted the job-description with the id descriptionId
     */
    public String getUsernameByDescriptionId(int descriptionId);
}
