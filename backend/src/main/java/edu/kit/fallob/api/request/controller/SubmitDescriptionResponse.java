package edu.kit.fallob.api.request.controller;

public class SubmitDescriptionResponse {

    private final int descriptionID;

    public SubmitDescriptionResponse(int descriptionID) {
        this.descriptionID = descriptionID;
    }

    public int getDescriptionID() {
        return descriptionID;
    }
}
