package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.FallobCommands;
import edu.kit.fallob.configuration.FallobConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A Rest Controller for getting the Fallob Configuration
 */
@RestController
@CrossOrigin
public class FallobConfigurationController {
    private static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    @Autowired
    private FallobCommands fallobConfigCommand;

    /**
     * A POST endpoint for getting the Fallob Configuration
     * @return sends a response with the configuration attributes or an error (including a status code and a message in json format)
     */
    @GetMapping("/api/v1/system/config")
    public ResponseEntity<Object> getFallobConfiguration() {
            FallobConfiguration fallobConfig  = fallobConfigCommand.getFallobConfiguration();
            Defaults defaults = new Defaults(fallobConfig.getDefaultJobPriority(), fallobConfig.getDefaultWallClockLimit(), fallobConfig.getDefaultContentMode());

            //convert the starting time into the right format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
            ZonedDateTime timeWithZone = fallobConfig.getStartTime().atZone(ZoneOffset.UTC);

            return ResponseEntity.ok(new FallobConfigurationsResponse(fallobConfig.getAmountProcesses(), timeWithZone.format(formatter), defaults));
    }
}
