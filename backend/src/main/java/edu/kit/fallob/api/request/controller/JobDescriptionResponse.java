package edu.kit.fallob.api.request.controller;

import java.util.List;

public class JobDescriptionResponse {

    private final List<String> description;

    public JobDescriptionResponse(List<String> description) {
        this.description = description;
    }

    public List<String> getDescription() {
        return description;
    }
}
