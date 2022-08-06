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
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/jobs/submit")
public class JobSubmitController {
    @Autowired
    private JobSubmitCommands jobSubmitCommand;

    @PostMapping("/url")
    public ResponseEntity<Object> submitJobWithUrlDescription(@RequestBody SubmitJobRequest request, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");
        URL url;
        File file;
        try {
            url = new URL(request.getUrl());
            file = new File(url.getFile());
            FileUtils.copyURLToFile(url, file);
        } catch (IOException | NullPointerException ioException) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, ioException.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        JobDescription jobDescription = new JobDescription(Collections.singletonList(file), SubmitType.URL);
        return getInclusiveCommandResponse(request, username, jobDescription);
    }

    private ResponseEntity<Object> getInclusiveCommandResponse(@RequestBody SubmitJobRequest request, String username, JobDescription jobDescription) {
        int jobId;
        try {
            jobId = jobSubmitCommand.submitJobWithDescriptionInclusive(username, jobDescription, request.getJobConfiguration());
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        } catch (NullPointerException exception) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        return ResponseEntity.ok(new SubmitJobResponse(jobId));
    }

    @PostMapping("/exclusive/config")
    public ResponseEntity<Object> submitJobWithSeparateDescription(@RequestBody SubmitJobRequest request, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");
        int jobNewId;
        try {
            jobNewId = jobSubmitCommand.submitJobWithDescriptionID(username, request.getJobConfiguration().getDescriptionID(), request.getJobConfiguration());
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        } catch (NullPointerException exception) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        return ResponseEntity.ok(new SubmitJobResponse(jobNewId));
    }
    @PostMapping("/inclusive")
    public ResponseEntity<Object> submitJobWithIncludedDescription(@RequestBody SubmitJobRequest request, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");
        List<File> files = new ArrayList<>();
        File file = new File("jobDescription.cnf");
        try {
            FileWriter myWriter = new FileWriter(file);
            List<String> lines = request.getJobDescription();
//            int counter = 0;
            for (String line : lines) {
                myWriter.write(line);
//                if (file.getTotalSpace() > Math.pow(10, 8)) {
//                    files.add(file);
//                    file = new File("jobDescription" + counter + ".cnf");
//                    counter++;
//                }
            }
            files.add(file);
            myWriter.close();
        } catch (IOException e) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, "An error occurred while creating a file with the job description.");
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        } catch (NullPointerException exception) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        JobDescription jobDescription = new JobDescription(files, SubmitType.INCLUSIVE);
        return getInclusiveCommandResponse(request, username, jobDescription);
    }

    @PostMapping("/restart/{jobId}")
    public ResponseEntity<Object> restartJob(@PathVariable int jobId, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");
        int jobNewId;
        try {
            jobNewId = jobSubmitCommand.restartCanceledJob(username, jobId);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        return ResponseEntity.ok(new SubmitJobResponse(jobNewId));

    }
    @PostMapping("/exclusive/description")
    public ResponseEntity<Object> saveDescription(MultipartFile file, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");
        if (file.isEmpty()) {
            String message = "Job description can not be empty";
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, message);
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        int descriptionId;
        File file1 = new File(file.getName());
        try {
            file.transferTo(file1);
        } catch (IOException ioException) {
        FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, ioException.getMessage());
        return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        JobDescription jobDescription = new JobDescription(Collections.singletonList(file1), SubmitType.EXCLUSIVE);
        try {
            descriptionId = jobSubmitCommand.saveJobDescription(username, jobDescription);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        return ResponseEntity.ok(new SubmitDescriptionResponse(descriptionId));
    }
}
