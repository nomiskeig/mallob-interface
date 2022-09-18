package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.JobSubmitCommands;
import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.dataobjects.SubmitType;
import edu.kit.fallob.springConfig.FallobException;
import edu.kit.fallob.springConfig.FallobWarning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kaloyan Enev
 * @version 1.0
 *          A Rest Controller for submitting jobs to Mallob
 */
@RestController
@CrossOrigin
@RequestMapping("/api/v1/jobs/submit")
public class JobSubmitController {
    @Autowired
    private JobSubmitCommands jobSubmitCommand;

    private static final String USERNAME = "username";

    private static final String JOB_DESCRIPTION_EMPTY = "Job description can not be empty";

    private static final String FILE_ERROR = "An error occurred while creating a file with the job description.";

    private static final String DESCRIPTION_IS_EMPTY = "The description can not be empty";

    private static final String FILE_NAME = "jobDescription";

    private static final String FILE_EXTENSION = ".cnf";

    private static final FallobConfiguration configuration = FallobConfiguration.getInstance();

    private static final String DIRECTORY_SEPARATOR = "/";

    private static int FILENAME_COUNTER = 0;

    /**
     * An POST endpoint for submitting a job where the description is given as an
     * url
     * Takes a request, parses the description to a file and forwards it together
     * with the configuration.
     * It is also responsible for system error handling
     * 
     * @param request     a request object containing an url and a configuration
     *                    object
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the id of the submitted job or an error
     *         (including a status code and a message in json format)
     */
    @PostMapping("/url")
    public ResponseEntity<Object> submitJobWithUrlDescription(@RequestBody SubmitJobRequest request,
            HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute(USERNAME);
        URL url;
        File file;
        try {
            url = new URL(request.getUrl());
            file = new File(configuration.getDescriptionsbasePath() + DIRECTORY_SEPARATOR + url.getFile());
            try (InputStream in = url.openStream();
                    BufferedInputStream bis = new BufferedInputStream(in);
                    FileOutputStream fos = new FileOutputStream(file.getName())) {

                byte[] data = new byte[1024];
                int count;
                while ((count = bis.read(data, 0, 1024)) != -1) {
                    fos.write(data, 0, count);
                }
            }
        } catch (IOException | NullPointerException ioException) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, ioException.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        JobDescription jobDescription = new JobDescription(Collections.singletonList(file), SubmitType.URL);
        boolean isInclusive = true;
        return submitJob(username, jobDescription, -1, request.getJobConfiguration(), isInclusive);
    }

    @PostMapping("/exclusive/config")
    public ResponseEntity<Object> submitJobWithSeparateDescription(@RequestBody SubmitJobRequest request,
            HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute(USERNAME);
        boolean isInclusive = false;
        return submitJob(username, null, request.getJobConfiguration().getDescriptionID(),
                request.getJobConfiguration(), isInclusive);
    }

    /**
     * Method for submitting of jobs
     *
     * @param username
     * @param description
     * @param descriptionID
     * @param config
     * @param isInclusive   if TRUE descriptionID is ignored and description is
     *                      used. If FALSE description is ignored and
     *                      descriptionID is used.
     * @return
     */
    private ResponseEntity<Object> submitJob(String username, JobDescription description, int descriptionID,
            JobConfiguration config, boolean isInclusive) {
        int newJobID;
        try {
            if (isInclusive) {
                newJobID = jobSubmitCommand.submitJobWithDescriptionInclusive(username, description, config);
            } else {
                newJobID = jobSubmitCommand.submitJobWithDescriptionID(username, descriptionID, config);
            }
        } catch (FallobException exception) {
            exception.printStackTrace();
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        } catch (NullPointerException | IllegalArgumentException exception) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        return ResponseEntity.ok(new SubmitJobResponse(newJobID));
    }

    @PostMapping("/inclusive")
    public ResponseEntity<Object> submitJobWithIncludedDescription(@RequestBody SubmitJobRequest request,
            HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute(USERNAME);
        List<File> files = new ArrayList<>();
        try {
            List<String> lines = request.getDescription();
            if (lines.isEmpty()) {
                FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, DESCRIPTION_IS_EMPTY);
                return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
            }
            for (String line : lines) {
                File file = new File(configuration.getDescriptionsbasePath() + DIRECTORY_SEPARATOR + FILE_NAME
                        + FILENAME_COUNTER + FILE_EXTENSION);
                FileWriter myWriter = new FileWriter(file.getAbsolutePath());
                myWriter.write(line);
                FILENAME_COUNTER++;
                files.add(file);
                myWriter.close();
            }
        } catch (IOException e) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, FILE_ERROR);
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        } catch (NullPointerException exception) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        JobDescription jobDescription = new JobDescription(files, SubmitType.INCLUSIVE);
        boolean isInclusive = true;
        return submitJob(username, jobDescription, -1, request.getJobConfiguration(), isInclusive);
    }

    @PostMapping("/restart/{jobId}")
    public ResponseEntity<Object> restartJob(@PathVariable int jobId, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute(USERNAME);
        int jobNewId;
        try {
            jobNewId = jobSubmitCommand.restartCanceledJob(username, jobId);
        } catch (FallobException exception) {
            exception.printStackTrace();
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        return ResponseEntity.ok(new SubmitJobResponse(jobNewId));

    }

    @PostMapping("/exclusive/description")
    public ResponseEntity<Object> saveDescription(@RequestParam("file") MultipartFile[] requestFiles,
            HttpServletRequest httpRequest) {
        List<File> files = new ArrayList<>();
        for (int i = 0; i < requestFiles.length; i++) {
            if (requestFiles[i].isEmpty()) {
                FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, JOB_DESCRIPTION_EMPTY);
                return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
            }
            File newFile = new File(
                    configuration.getDescriptionsbasePath() + DIRECTORY_SEPARATOR + "file" + FILENAME_COUNTER + ".cnf");
            FILENAME_COUNTER++;
            try {
                requestFiles[i].transferTo(newFile);
            } catch (IOException e) {
                FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, e.getMessage());
                return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
            }
            files.add(newFile);
        }
        String username = (String) httpRequest.getAttribute(USERNAME);
        JobDescription jobDescription = new JobDescription(files, SubmitType.EXCLUSIVE);
        int descriptionId;
        try {
            descriptionId = jobSubmitCommand.saveJobDescription(username,
                    jobDescription);
        } catch (FallobException exception) {
            exception.printStackTrace();
            FallobWarning warning = new FallobWarning(exception.getStatus(),
                    exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        return ResponseEntity.ok(new SubmitDescriptionResponse(descriptionId));

    }
}
