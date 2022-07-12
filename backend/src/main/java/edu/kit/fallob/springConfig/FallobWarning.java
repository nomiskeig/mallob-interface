package edu.kit.fallob.springConfig;

import org.springframework.http.HttpStatus;

public class FallobWarning {

    private HttpStatus status;
    private String message;

    public FallobWarning(HttpStatus status, String message) {
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
