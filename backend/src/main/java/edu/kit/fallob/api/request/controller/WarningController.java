package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.MallobCommands;
import edu.kit.fallob.mallobio.outputupdates.Warning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class WarningController {

    @Autowired
    private MallobCommands warningCommand;

    @GetMapping("/api/v1/system/mallobInfo")
    public ResponseEntity<Object> getMallobWarnings() {
        List<Warning> warnings = warningCommand.getWarnings();
        return ResponseEntity.ok(new WarningResponse(warnings));
    }
}
