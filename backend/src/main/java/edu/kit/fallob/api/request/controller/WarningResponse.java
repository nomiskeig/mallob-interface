package edu.kit.fallob.api.request.controller;

import java.util.List;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A response class for json parsing
 */
public class WarningResponse {
    private final List<WarningProxy> warnings;

    /**
     * Constructor that takes a list of warningProxies and sets the warning attribute
     * @param warnings the warningProxies
     */
    public WarningResponse(List<WarningProxy> warnings) {
        this.warnings = warnings;
    }

    /**
     * Getter
     * @return the warnings to be printed
     */
    public List<WarningProxy> getWarnings() {
        return warnings;
    }
}
