package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.JobSubmitCommands;
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
    public ResponseEntity<Object> submitJobWithUrlDescription(@RequestBody SubmitJobRequest request, HttpServletRequest httpRequest) {
        return null;
    }
    @RequestMapping()
    public ResponseEntity<Object> submitJobWithSeparateDescription(@RequestBody SubmitJobRequest request, HttpServletRequest httpRequest) {
        return null;
    }
    @RequestMapping()
    public ResponseEntity<Object> submitJobWithIncludedDescription(@RequestBody SubmitJobRequest request, HttpServletRequest httpRequest) {
        return null;
    }
    @RequestMapping()
    public ResponseEntity<Object> restartJob(@RequestParam int JobId, HttpServletRequest httpRequest) {
        return null;
    }
    @RequestMapping()
    public ResponseEntity<Object> saveDescription(MultipartFile file, HttpServletRequest httpRequest) {
        return null;
    }
}
