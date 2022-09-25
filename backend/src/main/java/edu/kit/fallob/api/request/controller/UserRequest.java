package edu.kit.fallob.api.request.controller;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A request class for json parsing
 */
public class UserRequest {
    private String username;
    private String password;
    private String email;

    /**
     * Standard constructor (for json parsing)
     */
    public UserRequest() {}

    /**
     * Constructor that sets the username and password (for the login endpoint)
     * @param username the provided username by the user
     * @param password the provided password by the user
     */
    public UserRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     *
     * @param username the provided username by the user
     * @param password the provided password by the user
     * @param email the provided email by the user
     */

    public UserRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    /**
     * Getter
     * @return the provided username by the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter
     * @param username the provided username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter
     * @return the provided password by the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter
     * @param password the provided password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter
     * @return the provided Email by the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter
     * @param email the provided Email
     */
    public void setEmail(String email) {
        this.email = email;
    }

}
