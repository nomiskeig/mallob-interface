package edu.kit.fallob.api.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
public class AbortJobController {
    @Autowired
    private JobAbortCommands jobAbortCommand;

    @RequestMapping()
    public ResponseEntity<Object> abortSingleJob(@RequestParam int jobId, HttpServletRequest httpRequest) {
        return null;
    }
    @RequestMapping()
    public ResponseEntity<Object> abortMultipleJobs(@RequestBody AbortJobRequest request, HttpServletRequest httpRequest) {
        return null;
    }
    @RequestMapping()
    public ResponseEntity<Object> abortAllJobs(HttpServletRequest httpRequest) {
        return null;
    }
    @RequestMapping()
    public ResponseEntity<Object> abortAllGlobalJobs(HttpServletRequest httpRequest) {
        return null;
    }
    @RequestMapping()
    public ResponseEntity<Object> abortIncrementalJob(@RequestParam int jobId, HttpServletRequest httpRequest) {
        return null;
    }
}
