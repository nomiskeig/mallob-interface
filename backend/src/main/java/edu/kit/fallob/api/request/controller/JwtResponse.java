package edu.kit.fallob.api.request.controller;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A response class for json parsing
 */
public class JwtResponse {

    private final String jwtToken;

    /**
     * Constructor
     * @param jwtToken an authentication Token in String format
     */
    public JwtResponse(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    /**
     * Getter
     * @return authentication token
     */
    public String getToken() {
        return jwtToken;
    }
}
