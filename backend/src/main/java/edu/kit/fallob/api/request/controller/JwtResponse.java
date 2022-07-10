package edu.kit.fallob.api.request.controller;

public class JwtResponse {

    private String JwtToken;

    public JwtResponse(String jwtToken) {
        JwtToken = jwtToken;
    }

    public String getToken() {
        return JwtToken;
    }
}
