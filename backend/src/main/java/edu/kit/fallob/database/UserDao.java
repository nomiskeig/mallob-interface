package edu.kit.fallob.database;

import edu.kit.fallob.dataobjects.User;
import edu.kit.fallob.springConfig.FallobException;

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
     * @throws FallobException if an error occurs while accessing the database
     */
    public void save(User user) throws FallobException;

    /**
     * removes a user from the database
     * @param username the name of the user that should be removed
     * @throws FallobException if an error occurs while accessing the database
     */
    public void remove(String username) throws FallobException;

    /**
     * getter for a user that is identified by the username
     * @param username the name of the user that should be returned
     * @return the suer object that contains the data
     * @throws FallobException if an error occurs while accessing the database or if the user couldn't be found
     */
    public User getUserByUsername(String username) throws FallobException;

    /**
     * returns the name of the user that submitted the job with the given id
     * @param jobId the id of the job for which the username should be returned
     * @return the name of the user that submitted the job with the id jobId
     * @throws @throws FallobException if an error occurs while accessing the database or if the user couldn't be found
     */
    public String getUsernameByJobId(int jobId) throws FallobException;

    /**
     * returns the name of the user that submitted the job-description with the given id
     * @param descriptionId the id of the job-description for which the username should be returned
     * @return the name of the user that submitted the job-description with the id descriptionId
     * @throws FallobException if an error occurs while accessing the database or if the user couldn't be found
     */
    public String getUsernameByDescriptionId(int descriptionId) throws FallobException;
}
