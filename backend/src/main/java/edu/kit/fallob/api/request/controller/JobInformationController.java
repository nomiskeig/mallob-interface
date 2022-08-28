package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.JobDescriptionCommands;
import edu.kit.fallob.commands.JobInformationCommands;
import edu.kit.fallob.commands.JobPendingCommand;
import edu.kit.fallob.commands.JobResultCommand;
import edu.kit.fallob.dataobjects.*;
import edu.kit.fallob.springConfig.FallobException;
import edu.kit.fallob.springConfig.FallobWarning;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A Rest Controller for getting types of information about the submitted jobs like Description, Information and Result
 */
@RestController
@CrossOrigin
@RequestMapping("/api/v1/jobs")
public class JobInformationController {
    @Autowired
    private JobInformationCommands jobInformationCommand;
    @Autowired
    private JobResultCommand jobResultCommand;
    @Autowired
    private JobDescriptionCommands jobDescriptionCommand;
    @Autowired
    private JobPendingCommand jobPendingCommmand;

    private static final String USERNAME = "username";

    private static final String FILE_CORRUPT = "One of the files is corrupted or could not be parsed";

    /**
     * An GET endpoint for getting Information about a single job
     * Takes a request with the id and username for a single job and forwards it. It is also responsible for system error handling
     * @param jobId the job id of the job, the Information of which is needed
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the Information of the requested job or an error (including a status code and a message in json format)
     */
    @GetMapping("/info/single/{jobId}")
    public ResponseEntity<Object> getSingleJobInformation(@PathVariable int jobId, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute(USERNAME);
        JobInformation jobInformation;
        try {
            jobInformation = jobInformationCommand.getSingleJobInformation(username, jobId);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        JobInformationProxy proxy = new JobInformationProxy(jobInformation);
        return ResponseEntity.ok(proxy);
    }

    /**
     * An POST endpoint for getting Information about multiple jobs
     * Takes a request with the id and username for multiple jobs and forwards it. It is also responsible for system error handling
     * @param request an JobInformationRequest object containing the job ids of the jobs, the Information of which is needed
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the Information of the requested jobs or an error (including a status code and a message in json format)
     */
    @PostMapping("/info")
    public ResponseEntity<Object> getMultipleJobInformation(@RequestBody JobInformationRequest request, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute(USERNAME);
        List<JobInformation> jobInformations;
        try {
            jobInformations = jobInformationCommand.getMultipleJobInformation(username, request.getJobs());
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        } catch (NullPointerException exception) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        List<JobInformationProxy> proxies = new ArrayList<>();
        for (JobInformation jobInfo : jobInformations) {
            proxies.add(new JobInformationProxy(jobInfo));
        }
        return ResponseEntity.ok(new JobInformationResponse(proxies));
    }

    /**
     * An GET endpoint for getting Information about all owned jobs
     * Takes a request with the username and forwards it. It is also responsible for system error handling
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the Information of all owned jobs or an error (including a status code and a message in json format)
     */
    @GetMapping("/info/all")
    public ResponseEntity<Object> getAllJobInformation(HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute(USERNAME);
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

    /**
     * An GET endpoint for getting Information about all jobs in the system (Available only for admins)
     * Takes a request with username and forwards it. It is also responsible for system error handling
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the Information of all jobs in the system or an error (including a status code and a message in json format)
     */
    @GetMapping("/info/global")
    public ResponseEntity<Object> getAllGlobalJobInformation(HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute(USERNAME);
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

    /**
     * An GET endpoint for getting the Description of a single job
     * Takes a request with the id and username for a single job and forwards it. It is also responsible for system error handling
     * @param jobId the job id of the job, the Description of which is needed
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the Description of the requested job (a zip file or a String List) or an error (including a status code and a message in json format)
     */
    @GetMapping(value = "/description/single/{jobId}")
    public ResponseEntity<Object> getSingleJobDescription(@PathVariable int jobId, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
        String username = (String) httpRequest.getAttribute(USERNAME);
        JobDescription jobDescriptions;
        try {
            jobDescriptions = jobDescriptionCommand.getSingleJobDescription(username, jobId);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        if (jobDescriptions.getSubmitType().equals(SubmitType.INCLUSIVE)) {
            List<File> files = jobDescriptions.getDescriptionFiles();
            List<String> description = new ArrayList<>();
            for (File file : files) {
                StringBuilder stringBuilder = new StringBuilder();
                try {
                    Scanner myReader = new Scanner(file);
                    boolean hasNextLine = myReader.hasNextLine();
                    while (hasNextLine) {
                        String nextLine = myReader.nextLine();
                        hasNextLine = myReader.hasNextLine();
                        stringBuilder.append(nextLine);
                        if (hasNextLine) {
                            stringBuilder.append("\n");
                        } else {
                            break;
                        }
                    }
                    myReader.close();
                } catch (FileNotFoundException e) {
                    FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, FILE_CORRUPT);
                    return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
                }
                description.add(stringBuilder.toString());
            }
            return ResponseEntity.ok(new JobDescriptionResponse(description));
        }
        else {
           return getDescriptionsZip(response, Collections.singletonList(jobDescriptions));
        }
    }

    /**
     * An POST endpoint for getting the Description of multiple jobs
     * Takes a request with the id and username for multiple jobs and forwards it. It is also responsible for system error handling
     * @param request an JobInformationRequest object containing the job ids of the jobs, the Description of which is needed
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the Description (in a zip file) of the requested jobs or an error (including a status code and a message in json format)
     */
    @PostMapping(value = "/description")
    public ResponseEntity<Object> getMultipleJobDescriptions(@RequestBody JobInformationRequest request, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
        String username = (String) httpRequest.getAttribute(USERNAME);
        List<JobDescription> jobDescriptions;
        try {
            jobDescriptions = jobDescriptionCommand.getMultipleJobDescription(username, request.getJobs());
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        } catch (NullPointerException exception) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        return getDescriptionsZip(response, jobDescriptions);
    }

    /**
     * An GET endpoint for getting the Description of all owned jobs
     * Takes a request with the username and forwards it. It is also responsible for system error handling
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the Description (in a zip file) of all owned jobs or an error (including a status code and a message in json format)
     */
    @GetMapping(value = "/description/all")
    public ResponseEntity<Object> getAllJobDescriptions(HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
        String username = (String) httpRequest.getAttribute(USERNAME);
        List<JobDescription> jobDescriptions;
        try {
            jobDescriptions = jobDescriptionCommand.getAllJobDescription(username);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        return getDescriptionsZip(response, jobDescriptions);
    }

    /**
     * An GET endpoint for getting the Result of a single job
     * Takes a request with the id and username for a single job and forwards it. It is also responsible for system error handling
     * @param jobId the job id of the job, the Result of which is needed
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the Result of the requested job (a zip file or a String List) or an error (including a status code and a message in json format)
     */
    @GetMapping(value ="/solution/single/{jobId}")
    public ResponseEntity<Object> getSingleJobResult(@PathVariable int jobId, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
        String username = (String) httpRequest.getAttribute(USERNAME);
        JobResult jobResult;
        try {
            jobResult = jobResultCommand.getSingleJobResult(username, jobId);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        return getResultsZip(response, Collections.singletonList(jobResult));
    }

    /**
     * An POST endpoint for getting the Result of multiple jobs
     * Takes a request with the id and username for multiple jobs and forwards it. It is also responsible for system error handling
     * @param request an JobInformationRequest object containing the job ids of the jobs, the Result of which is needed
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the Result (in a zip file) of the requested jobs or an error (including a status code and a message in json format)
     */
    @PostMapping("/solution")
    public ResponseEntity<Object> getMultipleJobResults(@RequestBody JobInformationRequest request, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
        String username = (String) httpRequest.getAttribute(USERNAME);
        List<JobResult> jobResults;
        try {
            jobResults = jobResultCommand.getMultipleJobResult(username, request.getJobs());
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        } catch (NullPointerException exception) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        return getResultsZip(response, jobResults);
    }

    /**
     * An GET endpoint for getting the Result of all owned jobs
     * Takes a request with the username and forwards it. It is also responsible for system error handling
     * @param httpRequest a servlet request that contains the username of the sender
     * @return sends a response with the Result (in a zip file) of all owned jobs or an error (including a status code and a message in json format)
     */
    @GetMapping(value = "/solution/all")
    public ResponseEntity<Object> getAllJobResults(HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
        String username = (String) httpRequest.getAttribute(USERNAME);
        List<JobResult> jobResults;
        try {
            jobResults = jobResultCommand.getAllJobResult(username);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        return getResultsZip(response, jobResults);
    }
    @GetMapping("/waitFor/{jobId}")
    public ResponseEntity<Object> waitForJob(@PathVariable int jobId, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute(USERNAME);
        ResultMetaData jobResult;
        try {
            jobResult = jobPendingCommmand.waitForJob(username, jobId);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        return ResponseEntity.ok(new JobPendingResponse(jobResult));
    }

    private ResponseEntity<Object> getDescriptionsZip(HttpServletResponse response, List<JobDescription> jobDescriptions) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/zip");
        response.setHeader("Content-disposition", "attachment; filename=description.zip");

        // Creating byteArray stream, make it bufferable and passing this buffer to ZipOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);


        // Packing files
        for (JobDescription jobDescription : jobDescriptions) {
            for (File file : jobDescription.getDescriptionFiles()) {
                // New zip entry and copying InputStream with file to ZipOutputStream, after all closing streams
                zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                FileInputStream fileInputStream = new FileInputStream(file);

                IOUtils.copy(fileInputStream, zipOutputStream);

                fileInputStream.close();
                zipOutputStream.closeEntry();
            }
        }

        if (zipOutputStream != null) {
            zipOutputStream.finish();
            zipOutputStream.flush();
            IOUtils.closeQuietly(zipOutputStream);
        }
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);

        return ResponseEntity.ok(byteArrayOutputStream.toByteArray());
    }

    private ResponseEntity<Object> getResultsZip(HttpServletResponse response, List<JobResult> jobResults) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/zip");
        response.setHeader("Content-disposition", "attachment; filename=description.zip");

        // Creating byteArray stream, making it bufferable and passing this buffer to ZipOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);


        // Packing files
        for (JobResult jobResult : jobResults) {
                File file = jobResult.getResult();
                // New zip entry and copy InputStream with file to ZipOutputStream, close streams
                zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                FileInputStream fileInputStream = new FileInputStream(file);

                IOUtils.copy(fileInputStream, zipOutputStream);

                fileInputStream.close();
                zipOutputStream.closeEntry();
            }

        if (zipOutputStream != null) {
            zipOutputStream.finish();
            zipOutputStream.flush();
            IOUtils.closeQuietly(zipOutputStream);
        }
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);

        return ResponseEntity.ok(byteArrayOutputStream.toByteArray());
    }
}
