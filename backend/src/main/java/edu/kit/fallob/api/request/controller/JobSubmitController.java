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
 * A Rest Controller for submitting jobs to Mallob
 */
@RestController
@CrossOrigin
@RequestMapping("/api/v1/jobs/submit")
public class JobSubmitController {
    @Autowired
    private JobSubmitCommands jobSubmitCommand;

    private static final String USERNAME = "username";

    private static final String JOB_DESCRIPTION_EMPTY = "The job description can not be empty.";

    private static final String FILE_ERROR = "An error occurred while creating a file with the job description.";

    private static final String DESCRIPTION_IS_EMPTY = "The description can not be empty.";

    private static final String FILE_NAME = "jobDescription";

    private static final String FILE_EXTENSION = ".cnf";

    private static final FallobConfiguration configuration = FallobConfiguration.getInstance();

    private static final String DIRECTORY_SEPARATOR = "/";

    private int filenameCounter = 0;

    /**
     * A POST endpoint for submitting a job where the description is given as an url
     * Takes a request, parses the description to a file and forwards it together with the configuration.
     * It is also responsible for system error handling
     *
     * @param request     a request object containing an url and a configuration object
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the id of the submitted job or an error (including a status code and a message in json format)
     */
    @PostMapping("/url")
    public ResponseEntity<Object> submitJobWithUrlDescription(@RequestBody SubmitJobRequest request, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute(USERNAME);
        URL url;
        File file;
        try {
            url = new URL(request.getUrl());
            // for tests
            if (configuration.getDescriptionsbasePath().isEmpty()) {
                file = new File(FILE_NAME + filenameCounter + FILE_EXTENSION);
            }
            // real case
            else {
                file = new File(configuration.getDescriptionsbasePath() + DIRECTORY_SEPARATOR + FILE_NAME
                        + filenameCounter + FILE_EXTENSION);
            }
            filenameCounter++;
            try (InputStream in = url.openStream();
                 BufferedInputStream bis = new BufferedInputStream(in);
                 FileOutputStream fos = new FileOutputStream(file.getAbsolutePath())) {

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

    /**
     * A POST endpoint for submitting a jobConfiguration
     * Takes a request, parses the configuration and forwards it.
     * It is also responsible for system error handling
     *
     * @param request     a request object containing the configuration attributes
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the id of the submitted job or an error (including a status code and a message in json format)
     */
    @PostMapping("/exclusive/config")
    public ResponseEntity<Object> submitJobWithSeparateDescription(@RequestBody SubmitJobRequest request,
                                                                   HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute(USERNAME);
        boolean isInclusive = false;
        return submitJob(username, null, request.getJobConfiguration().getDescriptionID(),
                request.getJobConfiguration(), isInclusive);
    }

    /**
     * Method for submitting the jobs
     *
     * @param username      the username of the user submitting the job
     * @param description   the jobDescription
     * @param descriptionID a description id if the description is already in the system
     * @param config        the jobConfiguration
     * @param isInclusive   if TRUE descriptionID is ignored and description is used. If FALSE description is ignored and
     *                      descriptionID is used.
     * @return returns a response with the id of the submitted job or an error (including a status code and a message in json format)
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

    /**
     * A POST endpoint for submitting a job where the description is included in the SubmitJobRequest
     * Takes a request, parses the description to a file and forwards it together with the configuration.
     * It is also responsible for system error handling
     *
     * @param request     a request object containing a list of Strings as the description and a configuration object
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the id of the submitted job or an error (including a status code and a message in json format)
     */
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
                File file;
                // for tests
                if (configuration.getDescriptionsbasePath().isEmpty()) {
                    file = new File(FILE_NAME + filenameCounter + FILE_EXTENSION);
                }
                // real case
                else {
                    file = new File(configuration.getDescriptionsbasePath() + DIRECTORY_SEPARATOR + FILE_NAME
                            + filenameCounter + FILE_EXTENSION);
                }
                filenameCounter++;
                try (FileWriter myWriter = new FileWriter(file.getAbsolutePath())) {
                    myWriter.write(line);
                    files.add(file);
                }
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

    /**
     * A POST endpoint for restarting an aborted single job
     * Takes a jobId as a path variable and forwards it. It is also responsible for system error handling
     *
     * @param jobId       the job id of the job, that is to be restarted
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the id of the restarted job or an error (including a status code and a message in json format)
     */
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

    /**
     * A POST endpoint for submitting a jobDescription separately
     * Takes a file, transforms it and forwards it.
     * It is also responsible for system error handling
     *
     * @param requestFiles a multipart file array containing the jobDescription
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the id of the submitted description or an error (including a status code and a message in json format)
     */
    @PostMapping("/exclusive/description")
    public ResponseEntity<Object> saveDescription(@RequestParam("file") MultipartFile[] requestFiles,
                                                  HttpServletRequest httpRequest) {
        List<File> files = new ArrayList<>();
        for (MultipartFile requestFile : requestFiles) {
            if (requestFile.isEmpty()) {
                FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, JOB_DESCRIPTION_EMPTY);
                return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
            }
            File newFile;
            // for tests
            if (configuration.getDescriptionsbasePath().isEmpty()) {
                newFile = new File(FILE_NAME + filenameCounter + FILE_EXTENSION);
            }
            // real case
            else {
                newFile = new File(configuration.getDescriptionsbasePath() + DIRECTORY_SEPARATOR + FILE_NAME
                        + filenameCounter + FILE_EXTENSION);
            }
            filenameCounter++;
            try {
                requestFile.transferTo(newFile);
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
            descriptionId = jobSubmitCommand.saveJobDescription(username, jobDescription);
        } catch (FallobException exception) {
            exception.printStackTrace();
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        return ResponseEntity.ok(new SubmitDescriptionResponse(descriptionId));
    }
}
