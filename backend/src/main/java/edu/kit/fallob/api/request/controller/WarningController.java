package edu.kit.fallob.api.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
public class WarningController {

    @Autowired
    private WarningCommand warningCommand;

    @RequestMapping
    public ResponseEntity<Object> getMallobWarnings(HttpServletRequest httpRequest) {
        return null;
    }
}