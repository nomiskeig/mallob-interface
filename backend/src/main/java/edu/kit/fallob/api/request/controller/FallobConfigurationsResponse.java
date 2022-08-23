package edu.kit.fallob.api.request.controller;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A response class for getting the Fallob Configuration
 */
public class FallobConfigurationsResponse {
    private final int amountProcesses;
    private final String startTime;
    private final Defaults defaults;

    /**
     * Constructor that takes the Fallob configuration and parses the needed attributes
     * @param amountProcesses amount of processes
     * @param startTime the start time
     * @param defaults the default attributes
     */
    public FallobConfigurationsResponse(int amountProcesses, String startTime, Defaults defaults) {
        this.amountProcesses = amountProcesses;
        this.startTime = startTime;
        this.defaults = defaults;
    }

    /**
     * Getter
     * @return amount of processes
     */
    public int getAmountProcesses() {
        return amountProcesses;
    }

    /**
     * Getter
     * @return the start time
     */
    public String getStartTime() {
        return startTime.toString();
    }

    /**
     * Getter
     * @return the default attributes
     */
    public Defaults getDefaults() {
        return defaults;
    }

}
