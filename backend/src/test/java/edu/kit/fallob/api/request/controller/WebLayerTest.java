package edu.kit.fallob.api.request.controller;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.fallob.commands.*;
import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.dataobjects.*;
import edu.kit.fallob.springConfig.FallobException;
import edu.kit.fallob.springConfig.JwtAuthenticationEntryPoint;
import edu.kit.fallob.springConfig.JwtTokenUtil;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebMvcTest
public class WebLayerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FallobCommands fallobCommands;

    @MockBean
    private MallobCommands mallobCommands;

    @MockBean
    private JobAbortCommands jobAbortCommands;

    @MockBean
    private JobResultCommand jobResultCommand;

    @MockBean
    private JobPendingCommmand jobPendingCommmand;

    @MockBean
    private JobDescriptionCommands jobDescriptionCommands;

    @MockBean
    private JobInformationCommands jobInformationCommands;

    @MockBean
    private JobSubmitCommands jobSubmitCommands;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Test
    @WithMockUser
    public void abortSingleJobSuccessfully() throws Exception {
        when(jobAbortCommands.abortSingleJob(null, 1)).thenReturn(true);

        this.mockMvc.perform(post("/api/v1/jobs/cancel/single/{jobId}", 1)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("\"OK\""));
    }

    @Test
    @WithMockUser
    public void abortSingleJobException() throws Exception {
        when(jobAbortCommands.abortSingleJob(null, 1)).thenThrow(new FallobException(HttpStatus.CONFLICT, "Job does not belong to this user"));

        this.mockMvc.perform(post("/api/v1/jobs/cancel/single/{jobId}", 1)).andDo(print())
                .andExpect(status().isConflict()).andExpect(content().string("{\"status\":\"CONFLICT\",\"message\":\"Job does not belong to this user\"}"));
    }

    @Test
    @WithMockUser
    public void abortSingleJobUnsuccessfully() throws Exception {
        when(jobAbortCommands.abortSingleJob(null, 1)).thenReturn(false);

        this.mockMvc.perform(post("/api/v1/jobs/cancel/single/{jobId}", 1)).andDo(print())
                .andExpect(status().isConflict()).andExpect(content().string("Job is not active"));
    }

    @Test
    @WithMockUser
    public void abortMultipleJobsSuccessfully() throws Exception {
        List<Integer> jobIds = new ArrayList<>();
        jobIds.add(1);
        jobIds.add(2);
        when(jobAbortCommands.abortMultipleJobs(null, jobIds)).thenReturn(jobIds);

        AbortJobRequest abortJobRequest = new AbortJobRequest(jobIds);
        this.mockMvc.perform(post("/api/v1/jobs/cancel/multiple").content(objectMapper.writeValueAsString(abortJobRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"jobIds\":[1,2]}"));
    }

    @Test
    @WithMockUser
    public void abortMultipleJobsException() throws Exception {
        List<Integer> jobIds = new ArrayList<>();
        jobIds.add(1);
        jobIds.add(2);
        when(jobAbortCommands.abortMultipleJobs(null, jobIds)).thenThrow(new FallobException(HttpStatus.NOT_FOUND, "Jobs not found"));

        AbortJobRequest abortJobRequest = new AbortJobRequest(jobIds);
        this.mockMvc.perform(post("/api/v1/jobs/cancel/multiple").content(objectMapper.writeValueAsString(abortJobRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isNotFound()).andExpect(content().string("{\"status\":\"NOT_FOUND\",\"message\":\"Jobs not found\"}"));
    }

    @Test
    @WithMockUser
    public void abortMultipleJobsUnsuccessfully() throws Exception {
        List<Integer> jobIds = new ArrayList<>();
        jobIds.add(1);
        jobIds.add(2);
        when(jobAbortCommands.abortMultipleJobs(null, jobIds)).thenReturn(new ArrayList<>());

        AbortJobRequest abortJobRequest = new AbortJobRequest(jobIds);
        this.mockMvc.perform(post("/api/v1/jobs/cancel/multiple").content(objectMapper.writeValueAsString(abortJobRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isConflict()).andExpect(content().string("No jobs are active"));
    }

    @Test
    @WithMockUser
    public void abortAllJobsSuccessfully() throws Exception {
        List<Integer> jobIds = new ArrayList<>();
        jobIds.add(1);
        jobIds.add(2);
        when(jobAbortCommands.abortAllJobs(null)).thenReturn(jobIds);

        this.mockMvc.perform(post("/api/v1/jobs/cancel/all")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"jobIds\":[1,2]}"));
    }

    @Test
    @WithMockUser(authorities = "NORMAL_USER")
    public void abortJobsGloballyForbidden() throws Exception {
        List<Integer> jobIds = new ArrayList<>();
        jobIds.add(1);
        jobIds.add(2);
        when(jobAbortCommands.abortAlGlobalJob(null)).thenReturn(jobIds);

        this.mockMvc.perform(post("/api/v1/jobs/cancel/global")).andDo(print())
                .andExpect(status().isForbidden());
    }
    @Test
    @WithMockUser(authorities = "ADMIN")
    public void abortJobsGloballySuccessful() throws Exception {
        List<Integer> jobIds = new ArrayList<>();
        jobIds.add(1);
        jobIds.add(2);
        when(jobAbortCommands.abortAlGlobalJob(null)).thenReturn(jobIds);

        this.mockMvc.perform(post("/api/v1/jobs/cancel/global")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"jobIds\":[1,2]}"));
    }

    @Test
    @WithMockUser
    public void abortIncrementalJob() throws Exception {
        when(jobAbortCommands.abortSingleJob(null, 1)).thenReturn(true);

        this.mockMvc.perform(post("/api/v1/jobs/cancel/incremental/{jobId}", 1)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("\"OK\""));
    }

    @Test
    @WithMockUser
    public void getFallobConfig() throws Exception {
        when(fallobCommands.getFallobConfiguration()).thenReturn(FallobConfiguration.getInstance());

        this.mockMvc.perform(get("/api/v1/system/config", 1)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"amountProcesses\":0,\"startTime\":null," +
                        "\"defaults\":{\"priority\":0.0,\"wallClockLimit\":null,\"contentMode\":null}}"));
    }

    @Test
    @WithMockUser
    public void getSingleJobInformationSuccessfully() throws Exception {
        List<Integer> dependencies = new ArrayList<>();
        dependencies.add(1);
        dependencies.add(2);
        List<Integer> jobIds = new ArrayList<>();
        jobIds.add(1);
        User user = new User("kalo", "1234", "kalo@gmail.com", 1, true, jobIds);
        ResultMetaData result = new ResultMetaData(1, 1, 1, 1, 1, 1);
        JobConfiguration jobConfig = new JobConfiguration("Job1", 1, "app", 1, 1, 1,
                1, dependencies, false, 1, 1,  "param");
        JobInformation jobInformation = new JobInformation(jobConfig, result, user, "12:34:32", JobStatus.DONE, 1);
        when(jobInformationCommands.getSingleJobInformation(null, 1)).thenReturn(jobInformation);

        this.mockMvc.perform(get("/api/v1/jobs/info/single/{jobId}", 1)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"jobInformation\":[{\"configuration\":" +
                        "{\"name\":\"Job1\",\"priority\":1.0,\"application\":\"app\",\"maxDemand\":1,\"wallClockLimit\":1.0," +
                        "\"cpuLimit\":1.0,\"arrival\":1.0,\"dependencies\":[1,2],\"incremental\":false,\"precursor\":1," +
                        "\"descriptionID\":1,\"additionalParameter\":\"param\"},\"email\":\"kalo@gmail.com\",\"username\":" +
                        "\"kalo\",\"submitTime\":\"12:34:32\",\"jobStatus\":\"DONE\",\"id\":1,\"resultMetaData\":{\"parsingTime\"" +
                        ":1.0,\"processingTime\":1.0,\"schedulingTime\":1.0,\"totalTime\":1.0,\"cpuSeconds\":1.0,\"wallclockSeconds\":1.0}}]}"));
    }

    @Test
    @WithMockUser
    public void getSingleJobInformationException() throws Exception {
        when(jobInformationCommands.getSingleJobInformation(null, 1)).thenThrow(new FallobException(HttpStatus.NOT_FOUND, "Job not found"));

        this.mockMvc.perform(get("/api/v1/jobs/info/single/{jobId}", 1)).andDo(print())
                .andExpect(status().isNotFound()).andExpect(content().string("{\"status\":\"NOT_FOUND\",\"message\":\"Job not found\"}"));
    }

    @Test
    @WithMockUser
    public void getMultipleJobInformationSuccessfully() throws Exception {
        List<Integer> dependencies = new ArrayList<>();
        dependencies.add(1);
        dependencies.add(2);
        List<Integer> jobIds = new ArrayList<>();
        jobIds.add(1);
        User user = new User("kalo", "1234", "kalo@gmail.com", 1, true, jobIds);
        ResultMetaData result = new ResultMetaData(1, 1, 1, 1, 1, 1);
        JobConfiguration jobConfig = new JobConfiguration("Job1", 1, "app", 1, 1, 1,
                1, dependencies, false, 1, 1,  "param");
        JobInformation jobInformation = new JobInformation(jobConfig, result, user, "12:34:32", JobStatus.DONE, 1);
        List<JobInformation> jobInformationList = new ArrayList<>();
        jobInformationList.add(jobInformation);
        when(jobInformationCommands.getMultipleJobInformation(null, jobIds)).thenReturn(jobInformationList);

        JobInformationRequest abortJobRequest = new JobInformationRequest(jobIds);
        this.mockMvc.perform(get("/api/v1/jobs/info").content(objectMapper.writeValueAsString(abortJobRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"jobInformation\":[{\"configuration\":" +
                        "{\"name\":\"Job1\",\"priority\":1.0,\"application\":\"app\",\"maxDemand\":1,\"wallClockLimit\":1.0," +
                        "\"cpuLimit\":1.0,\"arrival\":1.0,\"dependencies\":[1,2],\"incremental\":false,\"precursor\":1," +
                        "\"descriptionID\":1,\"additionalParameter\":\"param\"},\"email\":\"kalo@gmail.com\",\"username\":" +
                        "\"kalo\",\"submitTime\":\"12:34:32\",\"jobStatus\":\"DONE\",\"id\":1,\"resultMetaData\":{\"parsingTime\"" +
                        ":1.0,\"processingTime\":1.0,\"schedulingTime\":1.0,\"totalTime\":1.0,\"cpuSeconds\":1.0,\"wallclockSeconds\":1.0}}]}"));
    }

    @Test
    @WithMockUser
    public void getMultipleJobInformationException() throws Exception {
        List<Integer> jobIds = new ArrayList<>();
        jobIds.add(1);
        jobIds.add(2);
        when(jobInformationCommands.getMultipleJobInformation(null, jobIds)).thenThrow(new FallobException(HttpStatus.NOT_FOUND, "Jobs not found"));

        JobInformationRequest abortJobRequest = new JobInformationRequest(jobIds);
        this.mockMvc.perform(get("/api/v1/jobs/info").content(objectMapper.writeValueAsString(abortJobRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isNotFound()).andExpect(content().string("{\"status\":\"NOT_FOUND\",\"message\":\"Jobs not found\"}"));
    }

    @Test
    @WithMockUser
    public void getAllJobInformationSuccessfully() throws Exception {
        List<Integer> dependencies = new ArrayList<>();
        dependencies.add(1);
        dependencies.add(2);
        List<Integer> jobIds = new ArrayList<>();
        jobIds.add(1);
        User user = new User("kalo", "1234", "kalo@gmail.com", 1, true, jobIds);
        ResultMetaData result = new ResultMetaData(1, 1, 1, 1, 1, 1);
        JobConfiguration jobConfig = new JobConfiguration("Job1", 1, "app", 1, 1, 1,
                1, dependencies, false, 1, 1,  "param");
        JobInformation jobInformation = new JobInformation(jobConfig, result, user, "12:34:32", JobStatus.DONE, 1);
        List<JobInformation> jobInformationList = new ArrayList<>();
        jobInformationList.add(jobInformation);
        when(jobInformationCommands.getAllJobInformation(null)).thenReturn(jobInformationList);

        this.mockMvc.perform(get("/api/v1/jobs/info/all")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"jobInformation\":[{\"configuration\":" +
                        "{\"name\":\"Job1\",\"priority\":1.0,\"application\":\"app\",\"maxDemand\":1,\"wallClockLimit\":1.0," +
                        "\"cpuLimit\":1.0,\"arrival\":1.0,\"dependencies\":[1,2],\"incremental\":false,\"precursor\":1," +
                        "\"descriptionID\":1,\"additionalParameter\":\"param\"},\"email\":\"kalo@gmail.com\",\"username\":" +
                        "\"kalo\",\"submitTime\":\"12:34:32\",\"jobStatus\":\"DONE\",\"id\":1,\"resultMetaData\":{\"parsingTime\"" +
                        ":1.0,\"processingTime\":1.0,\"schedulingTime\":1.0,\"totalTime\":1.0,\"cpuSeconds\":1.0,\"wallclockSeconds\":1.0}}]}"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void getGlobalJobInformationSuccessfully() throws Exception {
        List<Integer> dependencies = new ArrayList<>();
        dependencies.add(1);
        dependencies.add(2);
        List<Integer> jobIds = new ArrayList<>();
        jobIds.add(1);
        User user = new User("kalo", "1234", "kalo@gmail.com", 1, true, jobIds);
        ResultMetaData result = new ResultMetaData(1, 1, 1, 1, 1, 1);
        JobConfiguration jobConfig = new JobConfiguration("Job1", 1, "app", 1, 1, 1,
                1, dependencies, false, 1, 1,  "param");
        JobInformation jobInformation = new JobInformation(jobConfig, result, user, "12:34:32", JobStatus.DONE, 1);
        List<JobInformation> jobInformationList = new ArrayList<>();
        jobInformationList.add(jobInformation);
        when(jobInformationCommands.getAllGlobalJobInformation(null)).thenReturn(jobInformationList);

        this.mockMvc.perform(get("/api/v1/jobs/info/global")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"jobInformation\":[{\"configuration\":" +
                        "{\"name\":\"Job1\",\"priority\":1.0,\"application\":\"app\",\"maxDemand\":1,\"wallClockLimit\":1.0," +
                        "\"cpuLimit\":1.0,\"arrival\":1.0,\"dependencies\":[1,2],\"incremental\":false,\"precursor\":1," +
                        "\"descriptionID\":1,\"additionalParameter\":\"param\"},\"email\":\"kalo@gmail.com\",\"username\":" +
                        "\"kalo\",\"submitTime\":\"12:34:32\",\"jobStatus\":\"DONE\",\"id\":1,\"resultMetaData\":{\"parsingTime\"" +
                        ":1.0,\"processingTime\":1.0,\"schedulingTime\":1.0,\"totalTime\":1.0,\"cpuSeconds\":1.0,\"wallclockSeconds\":1.0}}]}"));
    }

    @Test
    @WithMockUser(authorities = "NORMAL_USER")
    public void getGlobalJobInformationForbidden() throws Exception {
        List<Integer> dependencies = new ArrayList<>();
        dependencies.add(1);
        dependencies.add(2);
        List<Integer> jobIds = new ArrayList<>();
        jobIds.add(1);
        User user = new User("kalo", "1234", "kalo@gmail.com", 1, true, jobIds);
        ResultMetaData result = new ResultMetaData(1, 1, 1, 1, 1, 1);
        JobConfiguration jobConfig = new JobConfiguration("Job1", 1, "app", 1, 1, 1,
                1, dependencies, false, 1, 1,  "param");
        JobInformation jobInformation = new JobInformation(jobConfig, result, user, "12:34:32", JobStatus.DONE, 1);
        List<JobInformation> jobInformationList = new ArrayList<>();
        jobInformationList.add(jobInformation);
        when(jobInformationCommands.getAllGlobalJobInformation(null)).thenReturn(jobInformationList);

        this.mockMvc.perform(get("/api/v1/jobs/info/global")).andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void getSingleJobDescriptionString() throws Exception {
        File file = new File("description.cnf");
        FileWriter myWriter = new FileWriter(file);
        myWriter.write("Here would usually be the Literals in cnf format");
        myWriter.close();
        JobDescription jobDescription = new JobDescription(Collections.singletonList(file), SubmitType.INCLUSIVE);
        when(jobDescriptionCommands.getSingleJobDescription(null, 1)).thenReturn(jobDescription);

        this.mockMvc.perform(get("/api/v1/jobs/description/single/{jobId}", 1)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"jobDescription\":[\"Here would usually be the Literals in cnf format\"]}"));
    }

    @Test
    @WithMockUser
    public void getSingleJobDescriptionFile() throws Exception {
        File file = new File("description.cnf");
        FileWriter myWriter = new FileWriter(file);
        myWriter.write("Here would usually be the Literals in cnf format");
        myWriter.close();
        JobDescription jobDescription = new JobDescription(Collections.singletonList(file), SubmitType.EXCLUSIVE);
        when(jobDescriptionCommands.getSingleJobDescription(null, 1)).thenReturn(jobDescription);

        this.mockMvc.perform(get("/api/v1/jobs/description/single/{jobId}", 1)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"status\":\"NOT_FOUND\",\"message\":\"Job not found\"}"));
    }

    @Test
    @WithMockUser
    public void getSingleJobDescriptionException() throws Exception {
        when(jobDescriptionCommands.getSingleJobDescription(null, 1)).thenThrow(new FallobException(HttpStatus.NOT_FOUND, "Job not found"));

        this.mockMvc.perform(get("/api/v1/jobs/description/single/{jobId}", 1)).andDo(print())
                .andExpect(status().isNotFound()).andExpect(content().string("{\"status\":\"NOT_FOUND\",\"message\":\"Job not found\"}"));
    }




//    @Test
//    public void testWithSecurityMockMvcRequestPostProcessors() throws Exception {
//        this.mockMvc
//                .perform(
//                        post("/api/v1/users/login")
//                                .with(jwt())
//                                .with(csrf())
//                )
//                .andExpect(status().isOk());
//
//    }
}
