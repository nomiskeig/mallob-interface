package edu.kit.fallob.api.request.controller;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A response class for json parsing
 */
public class SubmitDescriptionResponse {

    private final int descriptionID;

    /**
     * Constructor that sets the descriptionID
     * @param descriptionID the id of the exclusive from the jobConfig saved description
     */
    public SubmitDescriptionResponse(int descriptionID) {
        this.descriptionID = descriptionID;
    }

    /**
     * Getter
     * @return the id of the exclusive from the jobConfig saved description
     */
    public int getDescriptionID() {
        return descriptionID;
    }
}
