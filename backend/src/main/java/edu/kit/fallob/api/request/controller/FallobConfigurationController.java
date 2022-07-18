package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.FallobCommands;
import edu.kit.fallob.configuration.FallobConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class FallobConfigurationController {
    @Autowired
    private FallobCommands fallobConfigCommand;

    @GetMapping("/api/v1/system/config")
    public ResponseEntity<Object> getFallobConfiguration() {
            FallobConfiguration fallobConfig  = fallobConfigCommand.getFallobConfiguration();
            Defaults defaults = new Defaults(fallobConfig.getDefaultJobPriority(), fallobConfig.getDefaultWallClockLimit(), fallobConfig.getDefaultContentMode());
            return ResponseEntity.ok(new FallobConfigurationsResponse(fallobConfig.getAmountProcesses(), fallobConfig.getStartTime(), defaults));
    }
}
