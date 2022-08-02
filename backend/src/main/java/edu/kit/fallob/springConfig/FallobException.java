package edu.kit.fallob.springConfig;

import org.springframework.http.HttpStatus;

/**
 * This class is used to throw custom exceptions in the Fallob system.
 */
public class FallobException extends Exception{

    private HttpStatus status;
    private String message;

    public FallobException(HttpStatus status, String message) {
        this.setStatus(status);
        this.setMessage(message);
    }

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
