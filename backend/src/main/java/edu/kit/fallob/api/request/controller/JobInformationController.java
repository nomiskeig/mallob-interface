package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.JobDescriptionCommands;
import edu.kit.fallob.commands.JobInformationCommands;
import edu.kit.fallob.commands.JobPendingCommmand;
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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    private JobPendingCommmand jobPendingCommmand;

    @GetMapping("/info/single/{jobId}")
    public ResponseEntity<Object> getSingleJobInformation(@PathVariable int jobId, HttpServletRequest httpRequest) {
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
    @GetMapping("/info")
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
    @GetMapping("/info/all")
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
    @GetMapping("/info/global")
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
    @GetMapping(value = "/description/single/{jobId}")
    public ResponseEntity<Object> getSingleJobDescription(@PathVariable int jobId, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
        String username = (String) httpRequest.getAttribute("username");
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
                try {
                    Scanner myReader = new Scanner(file);
                    while (myReader.hasNextLine()) {
                        description.add(myReader.nextLine());
                    }
                    myReader.close();
                } catch (FileNotFoundException e) {
                    FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, "File does not exist or is corrupt");
                    return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
                }
            }
            return ResponseEntity.ok(new JobDescriptionResponse(description));
        }
        else {
           return getDescriptionsZip(response, Collections.singletonList(jobDescriptions));
        }
    }
    @GetMapping(value = "/description")
    public ResponseEntity<Object> getMultipleJobDescriptions(@RequestBody JobInformationRequest request, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
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
    @GetMapping(value = "/description/all")
    public ResponseEntity<Object> getAllJobDescriptions(HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
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

    @GetMapping(value ="/solution/single/{jobId}")
    public ResponseEntity<Object> getSingleJobResult(@PathVariable int jobId, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
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

    @GetMapping("/solution")
    public ResponseEntity<Object> getMultipleJobResults(@RequestBody JobInformationRequest request, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
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
    @GetMapping(value = "/solution/all")
    public ResponseEntity<Object> getAllJobResults(HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
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
    @GetMapping("/waitFor/{jobId}")
    public ResponseEntity<Object> waitForJob(@PathVariable int jobId, HttpServletRequest httpRequest) {
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

    private ResponseEntity<Object> getDescriptionsZip(HttpServletResponse response, List<JobDescription> jobDescriptions) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/zip");

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
