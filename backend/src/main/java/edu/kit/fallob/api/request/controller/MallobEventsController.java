package edu.kit.fallob.api.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
public class MallobEventsController {
    @Autowired
    private EventCommand jobInformationCommand;
    @Autowired
    private SystemStateCommand jobResultCommand;

    @RequestMapping
    public ResponseEntity<Object> getMallobUpdates(@RequestBody MallobEventsRequest request, HttpServletRequest httpRequest) {
        return null;
    }
    @RequestMapping
    public ResponseEntity<Object> getSystemState(@RequestParam String time, HttpServletRequest httpRequest) {
        return null;
    }
}
