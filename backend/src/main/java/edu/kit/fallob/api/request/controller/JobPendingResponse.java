package edu.kit.fallob.api.request.controller;

public class JobPendingResponse {

    private boolean done;

    private ResultMetaData resultMetaData;


    public JobPendingResponse(boolean done, ResultMetaData resultMetaData) {
        this.done = done;
        this.resultMetaData = resultMetaData;
    }

    public boolean isDone() {
        return done;
    }

    public ResultMetaData getResultMetaData() {
        return resultMetaData;
    }
}
