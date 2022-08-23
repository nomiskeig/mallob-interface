package edu.kit.fallob.api.request.controller;

import java.util.List;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A response class for getting a job description in String format
 */
public class JobDescriptionResponse {

    private final List<String> description;

    /**
     * Constructor
     * @param description job description in String format
     */
    public JobDescriptionResponse(List<String> description) {
        this.description = description;
    }

    /**
     * Getter
     * @return job description in String format
     */
    public List<String> getDescription() {
        return description;
    }
}
