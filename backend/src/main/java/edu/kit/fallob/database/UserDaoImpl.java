package edu.kit.fallob.database;

import edu.kit.fallob.dataobjects.Admin;
import edu.kit.fallob.dataobjects.NormalUser;
import edu.kit.fallob.dataobjects.User;
import edu.kit.fallob.dataobjects.UserType;
import edu.kit.fallob.springConfig.FallobException;
import org.springframework.http.HttpStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * class that is responsible for storing all the user data
 * @author Valentin Schenk
 * @version 1.0
 */
public class UserDaoImpl implements UserDao{
    //error messages that are returned if an error occurs
    private static final String DATABASE_ERROR = "An error while accessing the database";
    private static final String DATABASE_NOT_FOUND = "Error, the requested entry couldn't be found";

    //sql queries that are required for the database interaction
    private static final String INSERT_USER = "INSERT INTO users (username, password, userType, priority, isVerified, email) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String REMOVE_USER = "DELETE FROM users WHERE username = ?";
    private static final String GET_USER = "SELECT * FROM users WHERE username = ?";
    private static final String USERNAME_BY_JOB_ID = "SELECT username FROM job WHERE jobId = ?";
    private static final String USERNAME_BY_DESCRIPTION_ID = "SELECT username FROM jobDescription WHERE descriptionId = ?";

    private final Connection conn;

    /**
     * constructor of the class
     * @throws FallobException if the connection to the database can't be established
     */
    public UserDaoImpl() throws FallobException {
        this.conn = DatabaseConnectionFactory.getConnection();
    }

    /**
     * saves a user in the database
     * @param user the user object that should be saved
     * @throws FallobException if an error occurs while accessing the database
     */
    @Override
    public void save(User user) throws FallobException {
        try {
            PreparedStatement statement = this.conn.prepareStatement(INSERT_USER);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getUserType().toString());
            statement.setDouble(4, user.getPriority());
            statement.setBoolean(5, user.isVerified());
            statement.setString(6, user.getEmail());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR);
        }
    }

    /**
     * removes a user from the database
     * @param username the name of the user that should be removed
     * @throws FallobException if an error occurs while accessing the database
     */
    @Override
    public void remove(String username) throws FallobException {
        try {
            PreparedStatement statement = this.conn.prepareStatement(REMOVE_USER);
            statement.setString(1, username);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR);
        }
    }

    /**
     * getter for a user that is identified by the username
     * @param username the name of the user that should be returned
     * @return the suer object that contains the data
     * @throws FallobException if an error occurs while accessing the database or if the user couldn't be found
     */
    @Override
    public User getUserByUsername(String username) throws FallobException {
        try {
            PreparedStatement statement = this.conn.prepareStatement(GET_USER);
            statement.setString(1, username);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                //start by index 2 because index 1 is the username
                String password = result.getString(2);
                UserType userType = UserType.valueOf(result.getString(3));
                double priority = result.getDouble(4);
                boolean isVerified = result.getBoolean(5);
                String email = result.getString(6);

                User returnUser;
                //this part is also ugly but the only way it's working
                if(userType.equals(UserType.ADMIN)) {
                    returnUser = new Admin(username, password, email);
                } else {
                    returnUser = new NormalUser(username, password, email);
                }

                returnUser.setPriority(priority);
                returnUser.setVerified(isVerified);

                return returnUser;
            } else {
                throw new FallobException(HttpStatus.NOT_FOUND, DATABASE_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR);
        }
    }

    /**
     * returns the name of the user that submitted the job with the given id
     * @param jobId the id of the job for which the username should be returned
     * @return the name of the user that submitted the job with the id jobId
     * @throws FallobException if an error occurs while accessing the database or if the user couldn't be found
     */
    @Override
    public String getUsernameByJobId(int jobId) throws FallobException {
        return getUsername(jobId, USERNAME_BY_JOB_ID);
    }

    /**
     * returns the name of the user that submitted the job-description with the given id
     * @param descriptionId the id of the job-description for which the username should be returned
     * @return the name of the user that submitted the job-description with the id descriptionId
     * @throws FallobException if an error occurs while accessing the database or if the user couldn't be found
     */
    @Override
    public String getUsernameByDescriptionId(int descriptionId) throws FallobException {
        return getUsername(descriptionId, USERNAME_BY_DESCRIPTION_ID);
    }

    /**
     * gets the username for either a given jobId or mallobId
     * @param id the jobId or mallobIf for which the username should be returned
     * @param sqlQuery the sql query that is used to get the information from the database
     * @return the username
     * @throws FallobException if an error occurs while accessing the database or if the user couldn't be found
     */
    private String getUsername(int id, String sqlQuery) throws FallobException {
        try {
            PreparedStatement statement = this.conn.prepareStatement(sqlQuery);
            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getString(1);
            } else {
                throw new FallobException(HttpStatus.NOT_FOUND, DATABASE_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR);
        }
    }
}
