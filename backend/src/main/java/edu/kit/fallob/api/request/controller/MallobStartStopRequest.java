package edu.kit.fallob.api.request.controller;

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
