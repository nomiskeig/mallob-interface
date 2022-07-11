package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.JobDescriptionCommands;
import edu.kit.fallob.commands.JobInformationCommands;
import edu.kit.fallob.commands.JobResultCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
public class JobInformationController {
    @Autowired
    private JobInformationCommands jobInformationCommand;
    @Autowired
    private JobResultCommand jobResultCommand;
    @Autowired
    private JobDescriptionCommands jobDescriptionCommand;
    @RequestMapping
    public ResponseEntity<Object> getSingleJobInformation(@RequestParam int jobId, HttpServletRequest httpRequest) {
        return null;
    }
    public ResponseEntity<Object> getMultipleJobInformation(@RequestBody JobInformationRequest request, HttpServletRequest httpRequest) {
        return null;
    }
    public ResponseEntity<Object> getAllJobInformation(HttpServletRequest httpRequest) {
        return null;
    }
    public ResponseEntity<Object> getAllGlobalJobInformation(HttpServletRequest httpRequest) {
        return null;
    }
    public ResponseEntity<Object> getSingleJobDescription(@RequestParam int jobId, HttpServletRequest httpRequest) {
        return null;
    }
    public ResponseEntity<Object> getMultipleJobDescriptions(JobInformationRequest request, HttpServletRequest httpRequest) {
        return null;
    }
    public ResponseEntity<Object> getAllJobDescriptions(HttpServletRequest request) {
        return null;
    }
    public ResponseEntity<Object> getSingleJobResult(@RequestParam int jobId, HttpServletRequest httpRequest) {
        return null;
    }
    public ResponseEntity<Object> getMultipleJobResults(JobInformationRequest request, HttpServletRequest httpRequest) {
        return null;
    }
    public ResponseEntity<Object> getAllJobResults(HttpServletRequest httpRequest) {
        return null;
    }
    public ResponseEntity<Object> waitForJob(@RequestParam int jobId, HttpServletRequest httpRequest) {
        return null;
    }
}
