package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.JobSubmitCommands;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.dataobjects.SubmitType;
import edu.kit.fallob.springConfig.FallobException;
import edu.kit.fallob.springConfig.FallobWarning;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;

@RestController
@CrossOrigin
public class JobSubmitController {
    @Autowired
    private JobSubmitCommands jobSubmitCommand;

    @RequestMapping()
    public ResponseEntity<Object> submitJobWithUrlDescription(@RequestBody SubmitJobRequest request, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("authorities");
        int jobId;
        URL url;
        File file;
        try {
            url = new URL(request.getUrl());
            file = new File(url.getFile());
            FileUtils.copyURLToFile(url, file);
        } catch (IOException ioException) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, ioException.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        JobDescription jobDescription = new JobDescription(Collections.singletonList(file), SubmitType.URL);
        try {
            jobId = jobSubmitCommand.submitJobWithDescription(username, jobDescription, request.getJobConfiguration());
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        return ResponseEntity.ok(jobId);
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
