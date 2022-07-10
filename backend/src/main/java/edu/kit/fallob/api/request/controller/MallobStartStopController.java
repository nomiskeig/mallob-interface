package edu.kit.fallob.api.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
public class MallobStartStopController {

    @Autowired
    private MallobCommands mallobCommands;

    @RequestMapping
    public ResponseEntity<Object> startMallob(HttpServletRequest httpRequest) {
        return null;
    }
    @RequestMapping
    public ResponseEntity<Object> stopMallob(HttpServletRequest httpRequest){
        return null;
    }
    @RequestMapping
    public ResponseEntity<Object> restartMallob(HttpServletRequest httpRequest){
        return null;
    }
}
