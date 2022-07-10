package edu.kit.fallob.api.request.controller;

public class SubmitDescriptionResponse {

    private int descriptionId;

    public SubmitDescriptionResponse(int descriptionId) {
        this.descriptionId = descriptionId;
    }

    public int getDescriptionId() {
        return descriptionId;
    }
}
