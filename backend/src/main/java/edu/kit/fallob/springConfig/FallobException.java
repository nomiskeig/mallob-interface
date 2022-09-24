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

	public FallobException(HttpStatus status, String message, Throwable cause) {
		this.setStatus(status);
		this.setMessage(message);
		this.initCause(cause);
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public void printStackTrace() {
		if (this.getCause() != null) {
			this.getCause().printStackTrace();
		} else {
			super.printStackTrace();
		}
	}
}
