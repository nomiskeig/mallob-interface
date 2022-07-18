package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.JobAbortCommands;
import edu.kit.fallob.springConfig.FallobException;
import edu.kit.fallob.springConfig.FallobWarning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/jobs/cancel")
public class AbortJobController {
    @Autowired
    private JobAbortCommands jobAbortCommand;

    @PostMapping("/single/{jobId}")
    public ResponseEntity<Object> abortSingleJob(@PathVariable int jobId, HttpServletRequest httpRequest) {
        return abortJob(jobId, httpRequest);
    }

    @PostMapping("")
    public ResponseEntity<Object> abortMultipleJobs(@RequestBody AbortJobRequest request, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");
        List<Integer> successfullyAborted;
        try {
            successfullyAborted = jobAbortCommand.abortMultipleJobs(username, request.getJobIds());
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        if (!successfullyAborted.isEmpty()) {
            return ResponseEntity.ok(new AbortJobResponse(successfullyAborted));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("");
        }
    }

    @PostMapping("/all")
    public ResponseEntity<Object> abortAllJobs(HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");
        List<Integer> successfullyAborted;
        try {
            successfullyAborted = jobAbortCommand.abortAllJobs(username);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        if (!successfullyAborted.isEmpty()) {
            return ResponseEntity.ok(new AbortJobResponse(successfullyAborted));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("");
        }
    }

    @PostMapping("/global")
    public ResponseEntity<Object> abortAllGlobalJobs(HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");
        List<Integer> successfullyAborted;
        try {
            successfullyAborted = jobAbortCommand.abortAlGlobalJob(username);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        if (!successfullyAborted.isEmpty()) {
            return ResponseEntity.ok(new AbortJobResponse(successfullyAborted));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("");
        }
    }

    @PostMapping("incremental/{jobId}")
    public ResponseEntity<Object> abortIncrementalJob(@PathVariable int jobId, HttpServletRequest httpRequest) {
        return abortJob(jobId, httpRequest);
    }

    private ResponseEntity<Object> abortJob(int jobId, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");
        boolean successful;
        try {
            successful = jobAbortCommand.abortSingleJob(username, jobId);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        if (successful) {
            return ResponseEntity.ok(HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("");
        }
    }
}
