package edu.kit.fallob.api.request.controller;

public class JobDescriptionResponse {

    private int descriptionId;

    public JobDescriptionResponse(int descriptionId) {
        this.descriptionId = descriptionId;
    }

    public int getDescriptionId() {
        return descriptionId;
    }
}
