package edu.kit.fallob.api.request.controller;

import java.util.List;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A Rest Controller for Aborting the processing of jobs
 */
public class WarningResponse {
    private final List<WarningProxy> warnings;

    public WarningResponse(List<WarningProxy> warnings) {
        this.warnings = warnings;
    }

    public List<WarningProxy> getWarnings() {
        return warnings;
    }
}
