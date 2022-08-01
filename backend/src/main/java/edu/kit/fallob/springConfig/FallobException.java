package edu.kit.fallob.springConfig;

import org.springframework.http.HttpStatus;

/**
 * This class is used to throw custom exceptions in the Fallob system.
 */
public class FallobException extends Exception{

    private final HttpStatus status;
    private final String message;

    public FallobException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
