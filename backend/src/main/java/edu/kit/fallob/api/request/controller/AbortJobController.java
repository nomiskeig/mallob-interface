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

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A Rest Controller for Aborting the processing of jobs
 */
@RestController
@CrossOrigin
@RequestMapping("/api/v1/jobs")
public class AbortJobController {
    @Autowired
    private JobAbortCommands jobAbortCommand;

    private static final String NO_JOBS_ACTIVE = "No jobs are active";

    private static final String JOB_NOT_ACTIVE = "Job is not active";

    private static final String USERNAME = "username";

    private static final String EMPTY_ARRAY = "The array must contain at least one jobId";


    /**
     * An POST endpoint for aborting the processing of a single job
     * Takes a request, parses the data needed to abort a single job and forwards it. It is also responsible for system error handling
     * @param jobId the job id of the job, that is to be aborted
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the id of the aborted job or an error (including a status code and a message in json format)
     */
    @PostMapping("/cancel/single/{jobId}")
    public ResponseEntity<Object> abortSingleJob(@PathVariable int jobId, HttpServletRequest httpRequest) {
        return abortJob(jobId, httpRequest);
    }

    /**
     * An POST endpoint for aborting the processing of multiple jobs
     * Takes a request, parses the data needed to abort multiple jobs and forwards it. It is also responsible for system error handling
     * @param request an AbortJobRequest object containing the job ids of the jobs, that are to be aborted
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the ids of the aborted jobs or an error (including a status code and a message in json format)
     */
    @PostMapping("/cancel")
    public ResponseEntity<Object> abortMultipleJobs(@RequestBody AbortJobRequest request, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute(USERNAME);
        List<Integer> successfullyAborted;
        try {
            int[] jobIds = request.getJobs();
            if (jobIds.length == 0) {
                FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, EMPTY_ARRAY);
                return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
            }
            successfullyAborted = jobAbortCommand.abortMultipleJobs(username, jobIds);
        } catch (FallobException exception) {
            exception.printStackTrace();
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        } catch (NullPointerException exception) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        if (!successfullyAborted.isEmpty()) {
            return ResponseEntity.ok(new AbortJobResponse(successfullyAborted));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(NO_JOBS_ACTIVE);
        }
    }

    /**
     * An POST endpoint for aborting the processing of all owned jobs
     * Takes a request, parses the data needed to abort all owned jobs and forwards it. It is also responsible for system error handling
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the ids of the aborted jobs or an error (including a status code and a message in json format)
     */
    @PostMapping("/cancel/all")
    public ResponseEntity<Object> abortAllJobs(HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute(USERNAME);
        List<Integer> successfullyAborted;
        try {
            successfullyAborted = jobAbortCommand.abortAllJobs(username);
        } catch (FallobException exception) {
            exception.printStackTrace();
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        return ResponseEntity.ok(new AbortJobResponse(successfullyAborted));
    }

    /**
     * An POST endpoint for aborting the processing of all jobs in the system (Available only for admins)
     * Takes a request, parses the data needed to abort all jobs and forwards it. It is also responsible for system error handling
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the ids of the aborted jobs or an error (including a status code and a message in json format)
     */
    @PostMapping("/cancel/global")
    public ResponseEntity<Object> abortAllGlobalJobs(HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute(USERNAME);
        List<Integer> successfullyAborted;
        try {
            successfullyAborted = jobAbortCommand.abortAllGlobalJob(username);
        } catch (FallobException exception) {
            exception.printStackTrace();
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        return ResponseEntity.ok(new AbortJobResponse(successfullyAborted));
    }

    /**
     * An POST endpoint for aborting the processing of an incremental job
     * Takes a request, parses the data needed to abort an incremental job and forwards it. It is also responsible for system error handling
     * @param jobId the job id of the job, that is to be aborted
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the ids of the aborted jobs or an error (including a status code and a message in json format)
     */
    @PostMapping("/incremental/{jobId}")
    public ResponseEntity<Object> abortIncrementalJob(@PathVariable int jobId, HttpServletRequest httpRequest) {
        return abortJob(jobId, httpRequest);
    }

    /**
     * Takes a jobId, parses the username from the httpRequest and forwards the data. It is also responsible for system error handling
     * @param jobId the job id of the job, that is to be aborted
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the ids of the aborted jobs or an error (including a status code and a message in json format)
     */
    private ResponseEntity<Object> abortJob(int jobId, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute(USERNAME);
        boolean successful;
        try {
            successful = jobAbortCommand.abortSingleJob(username, jobId);
        } catch (FallobException exception) {
            exception.printStackTrace();
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        if (successful) {
            return ResponseEntity.ok(HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(JOB_NOT_ACTIVE);
        }
    }
}
