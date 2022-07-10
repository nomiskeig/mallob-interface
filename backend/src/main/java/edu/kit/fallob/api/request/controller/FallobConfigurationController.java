package edu.kit.fallob.api.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
public class FallobConfigurationController {
    @Autowired
    private FallobConfigurationCommand fallobConfigCommand;

    @RequestMapping
    public ResponseEntity<Object> getFallobConfiguration(HttpServletRequest httpRequest) {
        return null;
    }

}
