package edu.kit.fallob.database;

import edu.kit.fallob.dataobjects.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoImpl implements UserDao{

    private static final String INSERT_USER = "INSERT INTO users (username, password, userType, priority, isVerified, email) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String REMOVE_USER = "DELETE FROM users WHERE username = ?";
    private static final String GET_USER = "SELECT * FROM users WHERE username = ?";
    private static final String USERNAME_BY_JOB_ID = "SELECT username FROM job WHERE jobId = ?";
    private static final String USERNAME_BY_DESCRIPTION_ID = "SELECT username FROM jobDescription WHERE descriptionIDd = ?";

    private final Connection conn;

    public UserDaoImpl() {
        this.conn = DatabaseConnectionFactory.getConnection();
    }
    @Override
    public void save(User user) {
        try {
            PreparedStatement statement = this.conn.prepareStatement(INSERT_USER);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            //TODO: userType undefined
            //statement.setString(3, user.);
            statement.setDouble(4, user.getPriority());
            statement.setBoolean(5, user.isVerified());
            statement.setString(6, user.getEmail());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(String username) {
        try {
            PreparedStatement statement = this.conn.prepareStatement(REMOVE_USER);
            statement.setString(1, username);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        try {
            PreparedStatement statement = this.conn.prepareStatement(GET_USER);
            statement.setString(1, username);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                //start by index 2 because index 1 is the username
                String password = result.getString(2);
                //TODO: userType undefined
                String userType = result.getString(3);
                double priority = result.getDouble(4);
                boolean isVerified = result.getBoolean(5);
                String email = result.getString(6);

                //TODO: constructor wrong
                //return new User();
                return null;
            } else {
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getUsernameByJobId(int jobId) {
        return getUsername(jobId, USERNAME_BY_JOB_ID);
    }

    @Override
    public String getUsernameByDescriptionId(int descriptionId) {
        return getUsername(descriptionId, USERNAME_BY_DESCRIPTION_ID);
    }

    private String getUsername(int descriptionId, String sqlQuery) {
        try {
            PreparedStatement statement = this.conn.prepareStatement(sqlQuery);
            statement.setInt(1, descriptionId);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getString(1);
            } else {
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
