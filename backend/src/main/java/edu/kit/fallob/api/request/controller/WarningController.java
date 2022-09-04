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

@RestController
@CrossOrigin
public class WarningController {

    @Autowired
    private MallobCommands warningCommand;

    @GetMapping("/api/v1/system/mallobInfo")
    public ResponseEntity<Object> getMallobWarnings() {
        List<Warning> warnings = null;
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
