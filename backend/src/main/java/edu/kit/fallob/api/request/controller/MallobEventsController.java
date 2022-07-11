package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.MallobCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
public class MallobEventsController {
    @Autowired
    private MallobCommands mallobCommands;

    @RequestMapping
    public ResponseEntity<Object> getMallobUpdates(@RequestBody MallobEventsRequest request, HttpServletRequest httpRequest) {
        return null;
    }
    @RequestMapping
    public ResponseEntity<Object> getSystemState(@RequestParam String time, HttpServletRequest httpRequest) {
        return null;
    }
}
