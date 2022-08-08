package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.dataobjects.ResultMetaData;

public class JobPendingResponse {

    private final ResultMetaData resultMetaData;


    public JobPendingResponse(ResultMetaData resultMetaData) {
        this.resultMetaData = resultMetaData;
    }

    public ResultMetaData getResultMetaData() {
        return resultMetaData;
    }
}
