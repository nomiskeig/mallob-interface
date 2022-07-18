package edu.kit.fallob.springConfig;

import org.springframework.http.HttpStatus;

public class FallobWarning {

    private final HttpStatus status;
    private final String message;

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
