package edu.kit.fallob.api.request.controller;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * Saves the default variables used in the response class (used for json parsing)
 */
public class Defaults {
    private final float priority;
    private final String wallClockLimit;
    private final String contentMode;


    /**
     * Constructor that sets all variables
     * @param priority job priority
     * @param wallClockLimit the time limit that a job may be processed for
     * @param contentMode the content of the job description (SAT for example)
     */
    public Defaults(float priority, String wallClockLimit, String contentMode) {
        this.priority = priority;
        this.wallClockLimit = wallClockLimit;
        this.contentMode = contentMode;
    }

    /**
     * Getter
     * @return job priority
     */
    public float getPriority() {
        return priority;
    }

    /**
     * Getter
     * @return the time limit that a job may be processed for
     */
    public String getWallClockLimit() {
        return wallClockLimit;
    }

    /**
     * Getter
     * @return the content of the job description (SAT for example)
     */
    public String getContentMode() {
        return contentMode;
    }
}
