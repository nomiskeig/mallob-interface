package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.MallobCommands;
import edu.kit.fallob.mallobio.outputupdates.Warning;
import edu.kit.fallob.springConfig.FallobException;
import edu.kit.fallob.springConfig.FallobWarning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A Rest Controller for requesting Mallob warnings
 */
@RestController
@CrossOrigin
public class WarningController {

    @Autowired
    private MallobCommands warningCommand;

    /**
     * A GET endpoint for getting the Warnings from Mallob
     * Responsible for system error handling
     * @return sends a response with the ResultMetaData of the job or an error (including a status code and a message in json format)
     */
    @GetMapping("/api/v1/system/mallobInfo")
    public ResponseEntity<Object> getMallobWarnings() {
        List<Warning> warnings;
        try {
            warnings = warningCommand.getWarnings();
        } catch (FallobException e) {
            e.printStackTrace();
            FallobWarning warning = new FallobWarning(e.getStatus(), e.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        List<WarningProxy> warningProxies = new ArrayList<>();
        for (Warning warning: warnings) {
            warningProxies.add(new WarningProxy(warning));
        }

        return ResponseEntity.ok(new WarningResponse(warningProxies));
    }
}
