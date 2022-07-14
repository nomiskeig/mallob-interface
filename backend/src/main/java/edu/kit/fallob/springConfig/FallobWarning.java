package edu.kit.fallob.springConfig;

import org.springframework.http.HttpStatus;

public class FallobWarning {

    private HttpStatus status;
    private String message;

    public FallobWarning(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
