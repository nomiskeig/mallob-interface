package edu.kit.fallob.api.request.controller;

public class SubmitDescriptionResponse {

    private final int descriptionId;

    public SubmitDescriptionResponse(int descriptionId) {
        this.descriptionId = descriptionId;
    }

    public int getDescriptionId() {
        return descriptionId;
    }
}
