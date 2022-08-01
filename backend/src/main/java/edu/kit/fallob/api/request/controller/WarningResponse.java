package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.mallobio.outputupdates.Warning;

import java.util.List;

public class WarningResponse {
    private final List<Warning> warnings;

    public WarningResponse(List<Warning> warnings) {
        this.warnings = warnings;
    }

    public List<Warning> getWarnings() {
        return warnings;
    }
}
