package edu.kit.fallob.api.request.controller;

public class MallobEventsRequest {
    private String startTime;
    private String endTime;

    public MallobEventsRequest() {}

    public MallobEventsRequest(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


}
