package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.JobDescriptionCommands;
import edu.kit.fallob.commands.JobInformationCommands;
import edu.kit.fallob.commands.JobResultCommand;
import edu.kit.fallob.dataobjects.JobInformation;
import edu.kit.fallob.springConfig.FallobException;
import edu.kit.fallob.springConfig.FallobWarning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        String username = (String) httpRequest.getAttribute("authorities");
        JobInformation jobInformation;
        try {
            jobInformation = jobInformationCommand.getSingleJobInformation(username, jobId);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        JobInformationProxy proxy = new JobInformationProxy(jobInformation);
        return ResponseEntity.ok(new JobInformationResponse(Collections.singletonList(proxy)));
    }
    @RequestMapping
    public ResponseEntity<Object> getMultipleJobInformation(@RequestBody JobInformationRequest request, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("authorities");
        List<JobInformation> jobInformations;
        try {
            jobInformations = jobInformationCommand.getMultipleJobInformation(username, request.getJobIds());
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        List<JobInformationProxy> proxies = new ArrayList<>();
        for (JobInformation jobInfo : jobInformations) {
            proxies.add(new JobInformationProxy(jobInfo));
        }
        return ResponseEntity.ok(new JobInformationResponse(proxies));
    }
    @RequestMapping
    public ResponseEntity<Object> getAllJobInformation(HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("authorities");
        List<JobInformation> jobInformations;
        try {
            jobInformations = jobInformationCommand.getAllJobInformation(username);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        List<JobInformationProxy> proxies = new ArrayList<>();
        for (JobInformation jobInfo : jobInformations) {
            proxies.add(new JobInformationProxy(jobInfo));
        }
        return ResponseEntity.ok(new JobInformationResponse(proxies));
    }
    @RequestMapping
    public ResponseEntity<Object> getAllGlobalJobInformation(HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("authorities");
        List<JobInformation> jobInformations;
        try {
            jobInformations = jobInformationCommand.getAllGlobalJobInformation(username);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        List<JobInformationProxy> proxies = new ArrayList<>();
        for (JobInformation jobInfo : jobInformations) {
            proxies.add(new JobInformationProxy(jobInfo));
        }
        return ResponseEntity.ok(new JobInformationResponse(proxies));
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
