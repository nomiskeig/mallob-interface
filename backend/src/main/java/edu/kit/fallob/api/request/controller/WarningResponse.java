package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.mallobio.outputupdates.Warning;

import java.util.List;

public class WarningResponse {
    private final List<WarningProxy> warnings;

    public WarningResponse(List<WarningProxy> warnings) {
        this.warnings = warnings;
    }

    public List<WarningProxy> getWarnings() {
        return warnings;
    }
}
