package edu.kit.fallob.api.request.controller;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A request class for json parsing
 */
public class MallobStartStopRequest {

    private String params;

    public MallobStartStopRequest(String params) {
        this.params = params;
    }

    public MallobStartStopRequest() {}

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
