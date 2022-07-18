package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.JobDescriptionCommands;
import edu.kit.fallob.commands.JobInformationCommands;
import edu.kit.fallob.commands.JobPendingCommmand;
import edu.kit.fallob.commands.JobResultCommand;
import edu.kit.fallob.dataobjects.JobDescription;
import edu.kit.fallob.dataobjects.JobInformation;
import edu.kit.fallob.dataobjects.JobResult;
import edu.kit.fallob.dataobjects.ResultMetaData;
import edu.kit.fallob.springConfig.FallobException;
import edu.kit.fallob.springConfig.FallobWarning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@CrossOrigin
public class JobInformationController {
    @Autowired
    private JobInformationCommands jobInformationCommand;
    @Autowired
    private JobResultCommand jobResultCommand;
    @Autowired
    private JobDescriptionCommands jobDescriptionCommand;
    @Autowired
    private JobPendingCommmand jobPendingCommmand;

    @RequestMapping
    public ResponseEntity<Object> getSingleJobInformation(@RequestParam int jobId, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");
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
        String username = (String) httpRequest.getAttribute("username");
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
        String username = (String) httpRequest.getAttribute("username");
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
        String username = (String) httpRequest.getAttribute("username");
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
    @RequestMapping
    public ResponseEntity<Object> getSingleJobDescription(@RequestParam int jobId, HttpServletRequest httpRequest, HttpServletResponse response) {
        String username = (String) httpRequest.getAttribute("username");
        Object jobDescriptions;
        try {
            jobDescriptions = jobDescriptionCommand.getSingleJobDescription(username, jobId);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        if (jobDescriptions instanceof JobDescription description) {
            return getDescriptionsZip(response, Collections.singletonList(description));
        }
        else {
            return ResponseEntity.ok(new JobDescriptionResponse((List<String>) jobDescriptions));
        }
    }
    @RequestMapping
    public ResponseEntity<Object> getMultipleJobDescriptions(@RequestBody JobInformationRequest request, HttpServletRequest httpRequest, HttpServletResponse response) {
        String username = (String) httpRequest.getAttribute("username");
        List<JobDescription> jobDescriptions;
        try {
            jobDescriptions = jobDescriptionCommand.getMultipleJobDescription(username, request.getJobIds());
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        return getDescriptionsZip(response, jobDescriptions);
    }
    @RequestMapping
    public ResponseEntity<Object> getAllJobDescriptions(HttpServletRequest httpRequest, HttpServletResponse response) {
        String username = (String) httpRequest.getAttribute("username");
        List<JobDescription> jobDescriptions;
        try {
            jobDescriptions = jobDescriptionCommand.getAllJobDescription(username);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        return getDescriptionsZip(response, jobDescriptions);
    }

    @RequestMapping
    public ResponseEntity<Object> getSingleJobResult(@RequestParam int jobId, HttpServletRequest httpRequest, HttpServletResponse response) {
        String username = (String) httpRequest.getAttribute("username");
        JobResult jobResult;
        try {
            jobResult = jobResultCommand.getSingleJobResult(username, jobId);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        return getResultsZip(response, Collections.singletonList(jobResult));
    }

    @RequestMapping
    public ResponseEntity<Object> getMultipleJobResults(@RequestBody JobInformationRequest request, HttpServletRequest httpRequest, HttpServletResponse response) {
        String username = (String) httpRequest.getAttribute("username");
        List<JobResult> jobResults;
        try {
            jobResults = jobResultCommand.getMultipleJobResult(username, request.getJobIds());
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        return getResultsZip(response, jobResults);
    }
    @RequestMapping
    public ResponseEntity<Object> getAllJobResults(HttpServletRequest httpRequest, HttpServletResponse response) {
        String username = (String) httpRequest.getAttribute("username");
        List<JobResult> jobResults;
        try {
            jobResults = jobResultCommand.getAllJobResult(username);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        return getResultsZip(response, jobResults);
    }
    @RequestMapping
    public ResponseEntity<Object> waitForJob(@RequestParam int jobId, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");
        ResultMetaData jobResult;
        try {
            jobResult = jobPendingCommmand.waitForJob(username, jobId);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        return ResponseEntity.ok(new JobPendingResponse(jobResult));
    }

    private ResponseEntity<Object> getDescriptionsZip(HttpServletResponse response, List<JobDescription> jobDescriptions) {
        int BUFFER_SIZE = 1024;

        StreamingResponseBody streamResponseBody = out -> {
            final ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
            ZipEntry zipEntry = null;
            InputStream inputStream = null;
            try {
                for (JobDescription jobDescription : jobDescriptions) {
                    for (File file : jobDescription.getDescriptionFiles()) {
                        zipEntry = new ZipEntry(file.getName());

                        inputStream = new FileInputStream(file);

                        zipOutputStream.putNextEntry(zipEntry);
                        byte[] bytes = new byte[BUFFER_SIZE];
                        int length;
                        while ((length = inputStream.read(bytes)) >= 0) {
                            zipOutputStream.write(bytes, 0, length);
                        }
                    }
                }
                // set zip size in response
                response.setContentLength((int) (zipEntry != null ? zipEntry.getSize() : 0));
            } catch (IOException e) {
//                logger.error("Exception while reading and streaming data {} ", e);
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (zipOutputStream != null) {
                    zipOutputStream.close();
                }
            }
        };
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=example.zip");
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Expires", "0");

        return ResponseEntity.ok(streamResponseBody);
    }

    private ResponseEntity<Object> getResultsZip(HttpServletResponse response, List<JobResult> jobResults) {
        int BUFFER_SIZE = 1024;

        StreamingResponseBody streamResponseBody = out -> {
            final ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
            ZipEntry zipEntry = null;
            InputStream inputStream = null;
            try {
                    for (JobResult result : jobResults) {
                        zipEntry = new ZipEntry(result.getResult().getName());

                        inputStream = new FileInputStream(result.getResult());

                        zipOutputStream.putNextEntry(zipEntry);
                        byte[] bytes = new byte[BUFFER_SIZE];
                        int length;
                        while ((length = inputStream.read(bytes)) >= 0) {
                            zipOutputStream.write(bytes, 0, length);
                        }
                    }
                // set zip size in response
                response.setContentLength((int) (zipEntry != null ? zipEntry.getSize() : 0));
            } catch (IOException e) {
//                logger.error("Exception while reading and streaming data {} ", e);
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (zipOutputStream != null) {
                    zipOutputStream.close();
                }
            }
        };

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=example.zip");
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Expires", "0");

        return ResponseEntity.ok(streamResponseBody);
    }
}
