package edu.kit.fallob.springConfig;

import org.springframework.http.HttpStatus;

public class ApiError {
    private HttpStatus status;
    private String message;

    public ApiError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    // Not sure if Spring needs getters in order to parse the errors in json here
//    public HttpStatus getStatus() {
//        return status;
//    }
//
//    public String getMessage() {
//        return message;
//    }
}
