package edu.kit.fallob.api.request.controller;

import java.util.List;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A response class for json parsing
 */
public class JobInformationResponse {

    private final List<JobInformationProxy> information;

    /**
     * Constructor that takes a List of JobInformationProxies
     * @param information the required information about a job
     */
    public JobInformationResponse(List<JobInformationProxy> information) {
        this.information = information;
    }

    /**
     * Getter
     * @return the required information about a job
     */
    public List<JobInformationProxy> getInformation() {
        return information;
    }
}
