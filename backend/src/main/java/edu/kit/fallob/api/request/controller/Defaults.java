package edu.kit.fallob.api.request.controller;

public class Defaults {
    private final float priority;
    private final String wallClockLimit;
    private final String contentMode;


    public Defaults(float priority, String wallClockLimit, String contentMode) {
        this.priority = priority;
        this.wallClockLimit = wallClockLimit;
        this.contentMode = contentMode;
    }

    public float getPriority() {
        return priority;
    }

    public String getWallClockLimit() {
        return wallClockLimit;
    }

    public String getContentMode() {
        return contentMode;
    }
}
