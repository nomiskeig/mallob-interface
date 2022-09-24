package edu.kit.fallob.api.request.controller;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A response class for json parsing
 */
public class SubmitDescriptionResponse {

    private final int descriptionID;

    public SubmitDescriptionResponse(int descriptionID) {
        this.descriptionID = descriptionID;
    }

    public int getDescriptionID() {
        return descriptionID;
    }
}
