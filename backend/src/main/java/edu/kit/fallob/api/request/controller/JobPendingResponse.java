package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.dataobjects.ResultMetaData;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A response class for json parsing
 */
public class JobPendingResponse {

    private final ResultMetaData resultMetaData;


    /**
     * Constructor that takes additional information about the solution of a job (metadata)
     * @param resultMetaData the additional information about a job (metadata)
     */
    public JobPendingResponse(ResultMetaData resultMetaData) {
        this.resultMetaData = resultMetaData;
    }

    /**
     * Getter
     * @return metadata
     */
    public ResultMetaData getResultMetaData() {
        return resultMetaData;
    }
}
