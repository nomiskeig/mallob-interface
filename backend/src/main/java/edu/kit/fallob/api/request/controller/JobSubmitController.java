package edu.kit.fallob.api.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
public class JobSubmitController {
    @Autowired
    private JobSubmitCommands jobSubmitCommand;

    @RequestMapping()
    public ResponseEntity<Object> submitJobWithUrlDescription(@RequestBody SubmitJobRequest jobRequest, HttpServletRequest request) {
        return null;
    }
    @RequestMapping()
    public ResponseEntity<Object> submitJobWithSeparateDescription(@RequestBody SubmitJobRequest jobRequest, HttpServletRequest request) {
        return null;
    }
    @RequestMapping()
    public ResponseEntity<Object> submitJobWithIncludedDescription(@RequestBody SubmitJobRequest jobRequest, HttpServletRequest request) {
        return null;
    }
    @RequestMapping()
    public ResponseEntity<Object> restartJob(@RequestParam int JobId, HttpServletRequest request) {
        return null;
    }
    @RequestMapping()
    public ResponseEntity<Object> saveDescription(MultipartFile file, HttpServletRequest request) {
        return null;
    }
}
