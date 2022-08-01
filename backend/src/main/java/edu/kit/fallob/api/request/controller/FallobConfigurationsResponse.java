package edu.kit.fallob.api.request.controller;

public class FallobConfigurationsResponse {
    private final int amountProcesses;
    private final String startTime;
    private final Defaults defaults;

    public FallobConfigurationsResponse(int amountProcesses, String startTime, Defaults defaults) {
        this.amountProcesses = amountProcesses;
        this.startTime = startTime;
        this.defaults = defaults;
    }

    public int getAmountProcesses() {
        return amountProcesses;
    }

    public String getStartTime() {
        return startTime;
    }

    public Defaults getDefaults() {
        return defaults;
    }

}
