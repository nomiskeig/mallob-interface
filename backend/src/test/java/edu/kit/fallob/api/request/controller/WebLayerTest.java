package edu.kit.fallob.api.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.fallob.commands.*;
import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.dataobjects.*;
import edu.kit.fallob.mallobio.outputupdates.Event;
import edu.kit.fallob.mallobio.outputupdates.Warning;
import edu.kit.fallob.springConfig.FallobException;
import edu.kit.fallob.springConfig.JwtAuthenticationEntryPoint;
import edu.kit.fallob.springConfig.JwtTokenUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class WebLayerTest {
    //TODO Divide in one test class for each controller
    //TODO JavaDoc

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
    private JobPendingCommand jobPendingCommand;

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

    @MockBean
    private AuthenticationManager authenticationManager;

    private static int[] jobIds;

    private static List<Integer> jobIdsList;
    private static JobConfiguration jobConfig;

    private static final String DESCRIPTION_CONTENT = "Here would usually be the Literals in cnf format";

    private static final String SOLUTION_CONTENT = "Here would usually be the solution in text format";

    private static final String NOT_FOUND = "Job not found";

    private static final String NOT_FOUND_MULTIPLE = "Jobs not found";

    private static final String NOT_FOUND_EXCEPTION = "{\"status\":\"NOT_FOUND\",\"message\":\"" + NOT_FOUND + "\"}";
    private static final String NOT_FOUND_EXCEPTION_MULTIPLE = "{\"status\":\"NOT_FOUND\",\"message\":\"" + NOT_FOUND_MULTIPLE + "\"}";

    private static final String JSON_JOB_INFORMATION = "{\"jobInformation\":[{\"configuration\":{\"name\":\"Job1\"," +
            "\"priority\":1.0,\"application\":\"application\",\"maxDemand\":1,\"wallClockLimit\":\"1.0\",\"cpuLimit\":\"1.0\"," +
            "\"arrival\":1.0,\"dependencies\":[1,2],\"dependenciesStrings\":null,\"contentMode\":null,\"interrupt\":false," +
            "\"incremental\":true,\"literals\":null,\"precursor\":-2147483647,\"precursorString\":null,\"assumptions\":null," +
            "\"done\":false,\"descriptionID\":1,\"additionalParameter\":null},\"email\":\"kalo@gmail.com\",\"username\":\"kalo\"," +
            "\"submitTime\":\"12:34:32\",\"jobStatus\":\"DONE\",\"id\":1,\"resultMetaData\":{\"parsingTime\":1.0," +
            "\"processingTime\":1.0,\"schedulingTime\":1.0,\"totalTime\":1.0,\"cpuSeconds\":1.0,\"wallclockSeconds\":1.0}}]}";



    @BeforeAll
    public static void setup() {
        jobIds = new int[2];
        jobIds[0] = 1;
        jobIds[1] = 2;
        jobIdsList = new ArrayList<>();
        jobIdsList.add(1);
        jobIdsList.add(2);
        Integer[] dependencies = new Integer[2];
        dependencies[0] = 1;
        dependencies[1] = 2;
        String[] params = new String[1];
        params[0] = "params";
        jobConfig = new JobConfiguration("Job1", 1, "application");
        jobConfig.setDescriptionID(1);
        jobConfig.setMaxDemand(1);
        //jobConfig.setDependencies(params);
        jobConfig.setIncremental(true);
        jobConfig.setDependencies(dependencies);
        jobConfig.setWallClockLimit(String.valueOf(1.0));
        jobConfig.setCpuLimit(String.valueOf(1.0));
        jobConfig.setArrival(1.0);
    }

    @Test
    @WithMockUser
    public void saveDescriptionSuccessfully() throws Exception {
        File file = new File("description.cnf");
        FileWriter myWriter = new FileWriter(file);
        myWriter.write(DESCRIPTION_CONTENT);
        myWriter.close();
        JobDescription jobDescription = new JobDescription(Collections.singletonList(file), SubmitType.EXCLUSIVE);
        MockMultipartFile multipartFile = new MockMultipartFile("file", "description.cnf",
                MediaType.TEXT_PLAIN_VALUE, DESCRIPTION_CONTENT.getBytes());

        when(jobSubmitCommands.saveJobDescription(null, jobDescription)).thenReturn(1);

        this.mockMvc.perform(multipart("/api/v1/jobs/submit/exclusive/description").file(multipartFile)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"descriptionId\":0}"));
    }

    @Test
    @WithMockUser
    public void saveEmptyDescriptionFile() throws Exception {
        File file = new File("description.cnf");
        JobDescription jobDescription = new JobDescription(Collections.singletonList(file), SubmitType.EXCLUSIVE);
        MockMultipartFile multipartFile = new MockMultipartFile("file", "description.cnf",
                MediaType.TEXT_PLAIN_VALUE, "".getBytes());

        when(jobSubmitCommands.saveJobDescription(null, jobDescription)).thenReturn(1);

        this.mockMvc.perform(multipart("/api/v1/jobs/submit/exclusive/description").file(multipartFile)).andDo(print())
                .andExpect(status().isBadRequest()).andExpect(content().string("{\"status\":\"BAD_REQUEST\"" +
                        ",\"message\":\"Job description can not be empty\"}"));
    }


    @Test
    @WithMockUser
    public void submitJobInclusiveSuccessfully() throws Exception {
        List<String> descriptionsList = new ArrayList<>();
        descriptionsList.add(DESCRIPTION_CONTENT);
        descriptionsList.add(DESCRIPTION_CONTENT);
        File file = new File("jobDescription.cnf");
        FileWriter myWriter = new FileWriter(file);
        for (String description : descriptionsList) {
            myWriter.write(description);
        }
        myWriter.close();
        List<File> filesList = new ArrayList<>();
        filesList.add(file);
        JobDescription jobDescription = new JobDescription(filesList, SubmitType.INCLUSIVE);

        // Using InvocationOnMock as mockito does not use the equals method of jobDescription
        when(jobSubmitCommands.submitJobWithDescriptionInclusive(isNull(), (any(JobDescription.class)), any(JobConfiguration.class)))
                .thenAnswer((Answer<Integer>) invocationOnMock -> {
                    JobDescription usedDescription = invocationOnMock.getArgument(1);
                    JobConfiguration usedConfig = invocationOnMock.getArgument(2);
                    boolean jobConfigEqualsUsedConfig = usedConfig.isDone() == jobConfig.isDone()
                            && usedConfig.isIncremental() == jobConfig.isIncremental() && usedConfig.isInterrupt()
                            == jobConfig.isInterrupt() && Objects.equals((usedConfig.getAdditionalParameter()), jobConfig.getAdditionalParameter())
                            && usedConfig.getApplication().equals(jobConfig.getApplication())
                            && Arrays.equals((usedConfig.getDependencies()), jobConfig.getDependencies()) && usedConfig.getPrecursor() == jobConfig.getPrecursor()
                            && usedConfig.getPriority() == jobConfig.getPriority() && usedConfig.getDescriptionID() == jobConfig.getDescriptionID()
                            && usedConfig.getName().equals(jobConfig.getName()) && Arrays.equals((usedConfig.getLiterals()), jobConfig.getLiterals())
                            && Objects.equals(usedConfig.getWallClockLimit(), jobConfig.getWallClockLimit()) && usedConfig.getMaxDemand() == jobConfig.getMaxDemand();

                    if (jobDescription.equals(usedDescription) && jobConfigEqualsUsedConfig) {
                        return 1;
                    } else {
                        return -1;
                    }
                });
        SubmitJobRequest submitJobRequest = new SubmitJobRequest(descriptionsList, jobConfig);

        this.mockMvc.perform(post("/api/v1/jobs/submit/inclusive").content(objectMapper.writeValueAsString(submitJobRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"jobId\":1}"));
    }

    @Test
    @WithMockUser
    public void submitJobConfigurationSuccessfully() throws Exception {

        // Using InvocationOnMock as mockito does not use the equals method of jobDescription
        when(jobSubmitCommands.submitJobWithDescriptionID(isNull(), anyInt(), any(JobConfiguration.class)))
                .thenAnswer((Answer<Integer>) invocationOnMock -> {
                    int descriptionId = invocationOnMock.getArgument(1);
                    JobConfiguration usedConfig = invocationOnMock.getArgument(2);
                    boolean jobConfigEqualsUsedConfig = usedConfig.isDone() == jobConfig.isDone()
                            && usedConfig.isIncremental() == jobConfig.isIncremental() && usedConfig.isInterrupt()
                            == jobConfig.isInterrupt() && Objects.equals((usedConfig.getAdditionalParameter()), jobConfig.getAdditionalParameter())
                            && usedConfig.getApplication().equals(jobConfig.getApplication())
                            && Arrays.equals((usedConfig.getDependencies()), jobConfig.getDependencies()) && usedConfig.getPrecursor() == jobConfig.getPrecursor()
                            && usedConfig.getPriority() == jobConfig.getPriority() && usedConfig.getDescriptionID() == jobConfig.getDescriptionID()
                            && usedConfig.getName().equals(jobConfig.getName()) && Arrays.equals((usedConfig.getLiterals()), jobConfig.getLiterals())
                            && Objects.equals(usedConfig.getWallClockLimit(), jobConfig.getWallClockLimit()) && usedConfig.getMaxDemand() == jobConfig.getMaxDemand();

                    if (descriptionId == jobConfig.getDescriptionID() && jobConfigEqualsUsedConfig) {
                        return 1;
                    } else {
                        return -1;
                    }
                });
        SubmitJobRequest submitJobRequest = new SubmitJobRequest(jobConfig);

        this.mockMvc.perform(post("/api/v1/jobs/submit/exclusive/config").content(objectMapper.writeValueAsString(submitJobRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"jobId\":1}"));
    }

    //TODO Test endpoints with user not verified - Only IntegrationTest possible?

    @Test
    @WithMockUser
    public void submitJobWithUrl() throws Exception {
        File file = new File("description.cnf");
        FileWriter myWriter = new FileWriter(file);
        myWriter.write(DESCRIPTION_CONTENT);
        myWriter.close();
        String url = file.toURI().toURL().toString();
        SubmitJobRequest submitJobRequest = new SubmitJobRequest(jobConfig, url);

        JobDescription jobDescription = new JobDescription(Collections.singletonList(file), SubmitType.EXCLUSIVE);

        when(jobSubmitCommands.submitJobWithDescriptionInclusive(isNull(), any(JobDescription.class), any(JobConfiguration.class)))
                .thenAnswer((Answer<Integer>) invocationOnMock -> {
                    JobDescription usedJobDescription = invocationOnMock.getArgument(1);
                    JobConfiguration usedConfig = invocationOnMock.getArgument(2);
                    boolean jobConfigEqualsUsedConfig = usedConfig.isDone() == jobConfig.isDone()
                            && usedConfig.isIncremental() == jobConfig.isIncremental() && usedConfig.isInterrupt()
                            == jobConfig.isInterrupt() && Objects.equals((usedConfig.getAdditionalParameter()), jobConfig.getAdditionalParameter())
                            && usedConfig.getApplication().equals(jobConfig.getApplication())
                            && Arrays.equals((usedConfig.getDependencies()), jobConfig.getDependencies()) && usedConfig.getPrecursor() == jobConfig.getPrecursor()
                            && usedConfig.getPriority() == jobConfig.getPriority() && usedConfig.getDescriptionID() == jobConfig.getDescriptionID()
                            && usedConfig.getName().equals(jobConfig.getName()) && Arrays.equals((usedConfig.getLiterals()), jobConfig.getLiterals())
                            && Objects.equals(usedConfig.getWallClockLimit(), jobConfig.getWallClockLimit()) && usedConfig.getMaxDemand() == jobConfig.getMaxDemand();
                    List<File> descriptionFiles = jobDescription.getDescriptionFiles();
                    List<File> usedDescriptionFiles = usedJobDescription.getDescriptionFiles();
                    boolean allFilesMatch = true;
                    outer:
                    for (int i = 0; i < descriptionFiles.size(); i++) {
                        BufferedReader bf1 = Files.newBufferedReader(descriptionFiles.get(i).toPath());
                        BufferedReader bf2 = Files.newBufferedReader(usedDescriptionFiles.get(i).toPath());

                            String line1, line2;
                            while ((line1 = bf1.readLine()) != null) {
                                line2 = bf2.readLine();
                                if (!line1.equals(line2)) {
                                    allFilesMatch = false;
                                    bf1.close();
                                    bf2.close();
                                    break outer;
                                }
                            }
                            bf1.close();
                            bf2.close();
                        }

                    if (allFilesMatch && jobConfigEqualsUsedConfig) {
                        return 1;
                    } else {
                        return -1;
                    }
                });

        this.mockMvc.perform(post("/api/v1/jobs/submit/url")
                .content(objectMapper.writeValueAsString(submitJobRequest)).contentType("application/json")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"jobId\":1}"));

    }


    @Test
    public void registerSuccessfully() throws Exception {
        String email = "kalo@student.kit.edu";
        String username = "kalo";
        String password = "1234";
        when(fallobCommands.register(email, username, password)).thenReturn(true);
        UserRequest userRequest = new UserRequest(email, username, password);

        this.mockMvc.perform(post("/api/v1/users/register").content(objectMapper.writeValueAsString(userRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("\"OK\""));
    }

    @Test
    public void registerException() throws Exception {
        String email = "kalo@student.kit.edu";
        String username = "kalo";
        String password = "1234";
        when(fallobCommands.register(email, username, password)).thenThrow(new FallobException(HttpStatus.CONFLICT, "Username already registered"));
        UserRequest userRequest = new UserRequest(email, username, password);

        this.mockMvc.perform(post("/api/v1/users/register").content(objectMapper.writeValueAsString(userRequest))
                        .contentType("application/json")).andDo(print()).andExpect(status().isConflict())
                .andExpect(content().string("{\"status\":\"CONFLICT\",\"message\":" + "\"Username already registered\"}"));
    }

    @Test
    public void loginSuccessfully() throws Exception {
        String username = "kalo";
        String password = "1234";
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, password, Collections.singleton(UserType.NORMAL_USER));
        when(fallobCommands.loadUserByUsername(username)).thenReturn(userDetails);
        UserRequest userRequest = new UserRequest(username, password);
        Authentication authentication = mock(Authentication.class);
        authentication.setAuthenticated(true);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("1234");

        this.mockMvc.perform(post("/api/v1/users/login").content(objectMapper.writeValueAsString(userRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"token\":\"1234\"}"));
    }

    @Test
    public void loginException() throws Exception {
        String username = "kalo";
        String password = "1234";
        when(fallobCommands.loadUserByUsername(username)).thenThrow(new UsernameNotFoundException("User not found"));
        UserRequest userRequest = new UserRequest(username, password);
        Authentication authentication = mock(Authentication.class);
        authentication.setAuthenticated(true);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        this.mockMvc.perform(post("/api/v1/users/login").content(objectMapper.writeValueAsString(userRequest))
                        .contentType("application/json")).andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string("{\"status\":\"NOT_FOUND\",\"message\":" + "\"User not found\"}"));
    }


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

        when(jobAbortCommands.abortMultipleJobs(null, jobIds)).thenReturn(jobIdsList);

        AbortJobRequest abortJobRequest = new AbortJobRequest(jobIds);
        this.mockMvc.perform(post("/api/v1/jobs/cancel/multiple").content(objectMapper.writeValueAsString(abortJobRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"jobIds\":[1,2]}"));
    }

    @Test
    @WithMockUser
    public void abortMultipleJobsException() throws Exception {
        when(jobAbortCommands.abortMultipleJobs(null, jobIds)).thenThrow(new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND_MULTIPLE));

        AbortJobRequest abortJobRequest = new AbortJobRequest(jobIds);
        this.mockMvc.perform(post("/api/v1/jobs/cancel/multiple").content(objectMapper.writeValueAsString(abortJobRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isNotFound()).andExpect(content().string(NOT_FOUND_EXCEPTION_MULTIPLE));
    }

    @Test
    @WithMockUser
    public void abortMultipleJobsUnsuccessfully() throws Exception {
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
        when(jobAbortCommands.abortAllGlobalJob(null)).thenReturn(jobIdsList);

        this.mockMvc.perform(post("/api/v1/jobs/cancel/global")).andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void abortJobsGloballySuccessful() throws Exception {
        List<Integer> jobIds = new ArrayList<>();
        jobIds.add(1);
        jobIds.add(2);
        when(jobAbortCommands.abortAllGlobalJob(null)).thenReturn(jobIds);

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
    	FallobConfiguration.resetConfig();
    	FallobConfiguration config = FallobConfiguration.getInstance();
        when(fallobCommands.getFallobConfiguration()).thenReturn(config);

        this.mockMvc.perform(get("/api/v1/system/config", 1)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"amountProcesses\":0,\"startTime\":null," +
                        "\"defaults\":{\"priority\":0.0,\"wallClockLimit\":null,\"contentMode\":null}}"));
    }

    
    @Test
    @WithMockUser
    public void getSingleJobInformationSuccessfully() throws Exception {
        User user = new NormalUser("kalo", "1234", "kalo@gmail.com");
        ResultMetaData result = new ResultMetaData(1, 1, 1, 1, 1, 1);
        JobInformation jobInformation = new JobInformation(jobConfig, result, user, "12:34:32", JobStatus.DONE, 1);
        when(jobInformationCommands.getSingleJobInformation(null, 1)).thenReturn(jobInformation);

        this.mockMvc.perform(get("/api/v1/jobs/info/single/{jobId}", 1)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(JSON_JOB_INFORMATION));
    }

    @Test
    @WithMockUser
    public void getSingleJobInformationException() throws Exception {
        when(jobInformationCommands.getSingleJobInformation(null, 1)).thenThrow(new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND));

        this.mockMvc.perform(get("/api/v1/jobs/info/single/{jobId}", 1)).andDo(print())
                .andExpect(status().isNotFound()).andExpect(content().string(NOT_FOUND_EXCEPTION));
    }

    @Test
    @WithMockUser
    public void getMultipleJobInformationSuccessfully() throws Exception {
        User user = new NormalUser("kalo", "1234", "kalo@gmail.com");
        ResultMetaData result = new ResultMetaData(1, 1, 1, 1, 1, 1);
        JobInformation jobInformation = new JobInformation(jobConfig, result, user, "12:34:32", JobStatus.DONE, 1);
        List<JobInformation> jobInformationList = new ArrayList<>();
        jobInformationList.add(jobInformation);
        when(jobInformationCommands.getMultipleJobInformation(null, jobIds)).thenReturn(jobInformationList);

        JobInformationRequest abortJobRequest = new JobInformationRequest(jobIds);
        this.mockMvc.perform(get("/api/v1/jobs/info").content(objectMapper.writeValueAsString(abortJobRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(JSON_JOB_INFORMATION));
    }

    @Test
    @WithMockUser
    public void getMultipleJobInformationException() throws Exception {
        when(jobInformationCommands.getMultipleJobInformation(null, jobIds)).thenThrow(new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND_MULTIPLE));

        JobInformationRequest abortJobRequest = new JobInformationRequest(jobIds);
        this.mockMvc.perform(get("/api/v1/jobs/info").content(objectMapper.writeValueAsString(abortJobRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isNotFound()).andExpect(content().string(NOT_FOUND_EXCEPTION_MULTIPLE));
    }

    @Test
    @WithMockUser
    public void getAllJobInformationSuccessfully() throws Exception {
        User user = new NormalUser("kalo", "1234", "kalo@gmail.com");
        ResultMetaData result = new ResultMetaData(1, 1, 1, 1, 1, 1);
        JobInformation jobInformation = new JobInformation(jobConfig, result, user, "12:34:32", JobStatus.DONE, 1);
        List<JobInformation> jobInformationList = new ArrayList<>();
        jobInformationList.add(jobInformation);
        when(jobInformationCommands.getAllJobInformation(null)).thenReturn(jobInformationList);

        this.mockMvc.perform(get("/api/v1/jobs/info/all")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(JSON_JOB_INFORMATION));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void getGlobalJobInformationSuccessfully() throws Exception {
        User user = new NormalUser("kalo", "1234", "kalo@gmail.com");
        ResultMetaData result = new ResultMetaData(1, 1, 1, 1, 1, 1);
        JobInformation jobInformation = new JobInformation(jobConfig, result, user, "12:34:32", JobStatus.DONE, 1);
        List<JobInformation> jobInformationList = new ArrayList<>();
        jobInformationList.add(jobInformation);
        when(jobInformationCommands.getAllGlobalJobInformation(null)).thenReturn(jobInformationList);

        this.mockMvc.perform(get("/api/v1/jobs/info/global")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(JSON_JOB_INFORMATION));
    }

    @Test
    @WithMockUser(authorities = "NORMAL_USER")
    public void getGlobalJobInformationForbidden() throws Exception {
        User user = new NormalUser("kalo", "1234", "kalo@gmail.com");
        ResultMetaData result = new ResultMetaData(1, 1, 1, 1, 1, 1);
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
        myWriter.write(DESCRIPTION_CONTENT);
        myWriter.close();
        JobDescription jobDescription = new JobDescription(Collections.singletonList(file), SubmitType.INCLUSIVE);
        when(jobDescriptionCommands.getSingleJobDescription(null, 1)).thenReturn(jobDescription);

        this.mockMvc.perform(get("/api/v1/jobs/description/single/{jobId}", 1)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"jobDescription\":[\"" + DESCRIPTION_CONTENT + "\"]}"));
    }

    @Test
    @WithMockUser
    public void getSingleJobDescriptionFile() throws Exception {
        File file = new File("description.cnf");
        FileWriter myWriter = new FileWriter(file);
        myWriter.write(DESCRIPTION_CONTENT);
        myWriter.close();
        JobDescription jobDescription = new JobDescription(Collections.singletonList(file), SubmitType.EXCLUSIVE);
        when(jobDescriptionCommands.getSingleJobDescription(null, 1)).thenReturn(jobDescription);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/jobs/description/single/{jobId}", 1)
                .contentType("application/zip")).andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
        Assertions.assertEquals(200, result.getResponse().getStatus());
        Assertions.assertEquals(194, result.getResponse().getContentAsByteArray().length);
        Assertions.assertEquals("application/zip", result.getResponse().getContentType());

    }

    @Test
    @WithMockUser
    public void getSingleJobDescriptionException() throws Exception {
        when(jobDescriptionCommands.getSingleJobDescription(null, 1)).thenThrow(new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND));

        this.mockMvc.perform(get("/api/v1/jobs/description/single/{jobId}", 1)).andDo(print())
                .andExpect(status().isNotFound()).andExpect(content().string(NOT_FOUND_EXCEPTION));
    }

    @Test
    @WithMockUser
    public void getMultipleJobDescriptionFile() throws Exception {
        File file = new File("description.cnf");
        File file2 = new File("description2.cnf");
        FileWriter myWriter = new FileWriter(file);
        myWriter.write(DESCRIPTION_CONTENT);
        myWriter.close();
        FileWriter myWriter2 = new FileWriter(file2);
        myWriter2.write(DESCRIPTION_CONTENT);
        myWriter2.close();

        JobDescription jobDescription = new JobDescription(Collections.singletonList(file), SubmitType.INCLUSIVE);
        JobDescription jobDescription2 = new JobDescription(Collections.singletonList(file2), SubmitType.EXCLUSIVE);
        List<JobDescription> jobDescriptionList = new ArrayList<>();
        jobDescriptionList.add(jobDescription);
        jobDescriptionList.add(jobDescription2);

        when(jobDescriptionCommands.getMultipleJobDescription(null, jobIds)).thenReturn(jobDescriptionList);
        JobInformationRequest jobDescriptionRequest = new JobInformationRequest(jobIds);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/jobs/description").content(objectMapper.writeValueAsString(jobDescriptionRequest))
                .contentType("application/json")).andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
        Assertions.assertEquals(200, result.getResponse().getStatus());
        Assertions.assertEquals(368, result.getResponse().getContentAsByteArray().length);
        Assertions.assertEquals("application/zip", result.getResponse().getContentType());
    }

    @Test
    @WithMockUser
    public void getMultipleJobDescriptionException() throws Exception {
        JobInformationRequest jobDescriptionRequest = new JobInformationRequest(jobIds);
        when(jobDescriptionCommands.getMultipleJobDescription(null, jobIds)).thenThrow(new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND_MULTIPLE));

        this.mockMvc.perform(get("/api/v1/jobs/description").content(objectMapper.writeValueAsString(jobDescriptionRequest))
                .contentType("application/json")).andDo(print()).andExpect(status().isNotFound()).andExpect(content()
                .string(NOT_FOUND_EXCEPTION_MULTIPLE));
    }

    @Test
    @WithMockUser
    public void getAllJobDescriptions() throws Exception {
        File file = new File("description.cnf");
        File file2 = new File("description2.cnf");
        FileWriter myWriter = new FileWriter(file);

        myWriter.write(DESCRIPTION_CONTENT);
        myWriter.close();
        FileWriter myWriter2 = new FileWriter(file2);
        myWriter2.write(DESCRIPTION_CONTENT);
        myWriter2.close();
        JobDescription jobDescription = new JobDescription(Collections.singletonList(file), SubmitType.INCLUSIVE);
        JobDescription jobDescription2 = new JobDescription(Collections.singletonList(file2), SubmitType.EXCLUSIVE);
        List<JobDescription> jobDescriptionList = new ArrayList<>();
        jobDescriptionList.add(jobDescription);
        jobDescriptionList.add(jobDescription2);

        when(jobDescriptionCommands.getAllJobDescription(null)).thenReturn(jobDescriptionList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/jobs/description/all"))
                .andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
        Assertions.assertEquals(200, result.getResponse().getStatus());
        Assertions.assertEquals(368, result.getResponse().getContentAsByteArray().length);
        Assertions.assertEquals("application/zip", result.getResponse().getContentType());
    }

    @Test
    @WithMockUser
    public void getSingleJobResultFile() throws Exception {
        File file = new File("solution.txt");
        FileWriter myWriter = new FileWriter(file);
        myWriter.write(SOLUTION_CONTENT);
        myWriter.close();
        JobResult jobResult = new JobResult(file);
        when(jobResultCommand.getSingleJobResult(null, 1)).thenReturn(jobResult);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/jobs/solution/single/{jobId}", 1)
                .contentType("application/zip")).andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
        Assertions.assertEquals(200, result.getResponse().getStatus());
        Assertions.assertEquals(188, result.getResponse().getContentAsByteArray().length);
        Assertions.assertEquals("application/zip", result.getResponse().getContentType());

    }

    @Test
    @WithMockUser
    public void getSingleJobResultException() throws Exception {
        when(jobResultCommand.getSingleJobResult(null, 1)).thenThrow(new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND));

        this.mockMvc.perform(get("/api/v1/jobs/solution/single/{jobId}", 1)).andDo(print())
                .andExpect(status().isNotFound()).andExpect(content().string(NOT_FOUND_EXCEPTION));
    }

    @Test
    @WithMockUser
    public void getMultipleJobResultFile() throws Exception {
        File file = new File("solution.txt");
        File file2 = new File("solution2.txt");
        FileWriter myWriter = new FileWriter(file);
        myWriter.write(SOLUTION_CONTENT);
        myWriter.close();
        FileWriter myWriter2 = new FileWriter(file2);
        myWriter2.write(SOLUTION_CONTENT);
        myWriter2.close();

        JobResult jobResult = new JobResult(file);
        JobResult jobResult2 = new JobResult(file2);
        List<JobResult> jobResultList = new ArrayList<>();
        jobResultList.add(jobResult);
        jobResultList.add(jobResult2);

        when(jobResultCommand.getMultipleJobResult(null, jobIds)).thenReturn(jobResultList);
        JobInformationRequest jobResultRequest = new JobInformationRequest(jobIds);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/jobs/solution").content(objectMapper.writeValueAsString(jobResultRequest))
                .contentType("application/json")).andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
        Assertions.assertEquals(200, result.getResponse().getStatus());
        Assertions.assertEquals(356, result.getResponse().getContentAsByteArray().length);
        Assertions.assertEquals("application/zip", result.getResponse().getContentType());
    }

    @Test
    @WithMockUser
    public void getMultipleJobResultException() throws Exception {
        JobInformationRequest jobResultRequest = new JobInformationRequest(jobIds);
        when(jobResultCommand.getMultipleJobResult(null, jobIds)).thenThrow(new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND_MULTIPLE));

        this.mockMvc.perform(get("/api/v1/jobs/solution").content(objectMapper.writeValueAsString(jobResultRequest))
                .contentType("application/json")).andDo(print()).andExpect(status().isNotFound()).andExpect(content()
                .string(NOT_FOUND_EXCEPTION_MULTIPLE));
    }

    @Test
    @WithMockUser
    public void getAllJobResults() throws Exception {
        File file = new File("solution.txt");
        File file2 = new File("solution2.txt");
        FileWriter myWriter = new FileWriter(file);
        myWriter.write(SOLUTION_CONTENT);
        myWriter.close();
        FileWriter myWriter2 = new FileWriter(file2);
        myWriter2.write(SOLUTION_CONTENT);
        myWriter2.close();
        JobResult jobResult = new JobResult(file);
        JobResult jobResult2 = new JobResult(file2);
        List<JobResult> jobResultList = new ArrayList<>();
        jobResultList.add(jobResult);
        jobResultList.add(jobResult2);

        when(jobResultCommand.getAllJobResult(null)).thenReturn(jobResultList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/jobs/solution/all"))
                .andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
        Assertions.assertEquals(200, result.getResponse().getStatus());
        Assertions.assertEquals(356, result.getResponse().getContentAsByteArray().length);
        Assertions.assertEquals("application/zip", result.getResponse().getContentType());
    }

    @Test
    @WithMockUser
    public void waitForJobSuccessfully() throws Exception {
        ResultMetaData result = new ResultMetaData(1, 1, 1, 1, 1, 1);
        when(jobPendingCommand.waitForJob(null, 1)).thenReturn(result);

        this.mockMvc.perform(get("/api/v1/jobs/waitFor/{jobId}", 1)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"resultMetaData\":{\"parsingTime\":1.0," +
                        "\"processingTime\":1.0,\"schedulingTime\":1.0,\"totalTime\":1.0,\"cpuSeconds\":1.0,\"wallclockSeconds\":1.0}}"));
    }

    @Test
    @WithMockUser
    public void waitForJobException() throws Exception {
        when(jobPendingCommand.waitForJob(null, 1)).thenThrow(new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND));

        this.mockMvc.perform(get("/api/v1/jobs/waitFor/{jobId}", 1)).andDo(print())
                .andExpect(status().isNotFound()).andExpect(content().string(NOT_FOUND_EXCEPTION));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void getWarnings() throws Exception {
        Warning warning = new Warning("Here would be a log line");
        when(mallobCommands.getWarnings()).thenReturn(Collections.singletonList(warning));

        this.mockMvc.perform(get("/api/v1/system/mallobInfo", 1)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"warnings\":[{\"logLine\":\"Here would be a log line\"}]}"));
    }

    @Test
    @WithMockUser(authorities = "NORMAL_USER")
    public void getWarningsForbidden() throws Exception {
        Warning warning = new Warning("Here would be a log line");
        when(mallobCommands.getWarnings()).thenReturn(Collections.singletonList(warning));

        this.mockMvc.perform(get("/api/v1/system/mallobInfo", 1)).andDo(print())
                .andExpect(status().isForbidden()).andExpect(content().string(""));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void startMallobSuccessfully() throws Exception {
        String params = "Here would be parameters";
        when(mallobCommands.startMallob(params)).thenReturn(true);
        MallobStartStopRequest mallobStartStopRequest = new MallobStartStopRequest(params);

        this.mockMvc.perform(post("/api/v1/system/mallob/start").content(objectMapper.writeValueAsString(mallobStartStopRequest))
                .contentType("application/json")).andDo(print()).andExpect(status().isOk()).andExpect(content().string("\"OK\""));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void startMallobUnsuccessfully() throws Exception {
        String params = "Here would be parameters";
        when(mallobCommands.startMallob(params)).thenReturn(false);
        MallobStartStopRequest mallobStartStopRequest = new MallobStartStopRequest(params);

        this.mockMvc.perform(post("/api/v1/system/mallob/start").content(objectMapper.writeValueAsString(mallobStartStopRequest))
                .contentType("application/json")).andDo(print()).andExpect(status().isConflict()).andExpect(content().string("The system is already running"));
    }

    @Test
    @WithMockUser(authorities = "NORMAL_USER")
    public void startMallobForbidden() throws Exception {
        String params = "Here would be parameters";
        when(mallobCommands.startMallob(params)).thenReturn(true);
        MallobStartStopRequest mallobStartStopRequest = new MallobStartStopRequest(params);

        this.mockMvc.perform(post("/api/v1/system/mallob/start").content(objectMapper.writeValueAsString(mallobStartStopRequest))
                .contentType("application/json")).andDo(print()).andExpect(status().isForbidden()).andExpect(content().string(""));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void stopMallobSuccessfully() throws Exception {
        when(mallobCommands.stopMallob()).thenReturn(true);

        this.mockMvc.perform(post("/api/v1/system/mallob/stop")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("\"OK\""));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void stopMallobUnsuccessfully() throws Exception {
        when(mallobCommands.stopMallob()).thenReturn(false);

        this.mockMvc.perform(post("/api/v1/system/mallob/stop")).andDo(print()).andExpect(status().isConflict())
                .andExpect(content().string("The system is not running"));
    }

    @Test
    @WithMockUser(authorities = "NORMAL_USER")
    public void stopMallobForbidden() throws Exception {
        when(mallobCommands.stopMallob()).thenReturn(true);

        this.mockMvc.perform(post("/api/v1/system/mallob/stop")).andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(""));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void restartMallobSuccessfully() throws Exception {
        String params = "Here would be parameters";
        when(mallobCommands.stopMallob()).thenReturn(true);
        when(mallobCommands.startMallob(params)).thenReturn(true);
        MallobStartStopRequest mallobStartStopRequest = new MallobStartStopRequest(params);

        this.mockMvc.perform(post("/api/v1/system/mallob/restart").content(objectMapper.writeValueAsString(mallobStartStopRequest))
                .contentType("application/json")).andDo(print()).andExpect(status().isOk()).andExpect(content().string("\"OK\""));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void restartMallobUnsuccessfully() throws Exception {
        String params = "Here would be parameters";
        when(mallobCommands.stopMallob()).thenReturn(false);
        when(mallobCommands.startMallob(params)).thenReturn(true);
        MallobStartStopRequest mallobStartStopRequest = new MallobStartStopRequest(params);

        this.mockMvc.perform(post("/api/v1/system/mallob/restart").content(objectMapper.writeValueAsString(mallobStartStopRequest))
                .contentType("application/json")).andDo(print()).andExpect(status().isConflict()).andExpect(content().string("The system is not running"));
    }

    @Test
    @WithMockUser(authorities = "NORMAL_USER")
    public void restartMallobForbidden() throws Exception {
        String params = "Here would be parameters";
        when(mallobCommands.stopMallob()).thenReturn(true);
        when(mallobCommands.startMallob(params)).thenReturn(true);
        MallobStartStopRequest mallobStartStopRequest = new MallobStartStopRequest(params);

        this.mockMvc.perform(post("/api/v1/system/mallob/restart").content(objectMapper.writeValueAsString(mallobStartStopRequest))
                .contentType("application/json")).andDo(print()).andExpect(status().isForbidden()).andExpect(content().string(""));
    }

    @Test
    @WithMockUser
    public void getMallobEventsSuccessfully() throws Exception {
        String startTime = "2020-02-13T18:51:09.840Z";
        String endTime = "2020-02-13T18:54:09.234Z";
        List<Event> eventsList = new ArrayList<>();
        LocalDateTime timeBetween = LocalDateTime.of(2020, 2, 13, 18, 52, 9);
        Event event = new Event(1, 1, 1, true, timeBetween);
        eventsList.add(event);

        when(mallobCommands.getEvents(startTime, endTime)).thenReturn(eventsList);

        this.mockMvc.perform(get("/api/v1/events/events?startTime=" + startTime + "&endTime=" + endTime))
                .andDo(print()).andExpect(status().isOk()).andExpect(content().string("{\"events\":[{" +
                        "\"processID\":1,\"treeIndex\":1,\"jobID\":1,\"load\":true,\"time\":\"2020-02-13T18:52:09\"}]}"));
    }

    @Test
    @WithMockUser
    public void getMallobEventsException() throws Exception {
        when(mallobCommands.getEvents("1", "2")).thenThrow(new FallobException(HttpStatus.NOT_FOUND, "Time point not valid"));

        this.mockMvc.perform(get("/api/v1/events/events?startTime=1&endTime=2"))
                .andDo(print()).andExpect(status().isNotFound()).andExpect(content().string("{\"status\":\"NOT_FOUND\"" +
                        ",\"message\":\"Time point not valid\"}"));
    }

    @Test
    @WithMockUser
    public void getSystemStateSuccessfully() throws Exception {
        String time = "2020-02-13T18:51:09.840Z";
        List<Event> eventsList = new ArrayList<>();
        LocalDateTime timeBetween = LocalDateTime.of(2020, 2, 13, 18, 52, 9);
        Event event = new Event(1, 1, 1, true, timeBetween);
        eventsList.add(event);
        SystemState systemState = new SystemState(LocalDateTime.now());
        systemState.setSystemState(eventsList);

        when(mallobCommands.getSystemState(time)).thenReturn(systemState);

        this.mockMvc.perform(get("/api/v1/events/state?time=" + time))
                .andDo(print()).andExpect(status().isOk()).andExpect(content().string("{\"events\":[{" +
                        "\"processID\":1,\"treeIndex\":1,\"jobID\":1,\"load\":true,\"time\":\"2020-02-13T18:52:09\"}]}"));
    }

    @Test
    @WithMockUser
    public void getSystemStateException() throws Exception {
        when(mallobCommands.getSystemState("1")).thenThrow(new FallobException(HttpStatus.NOT_FOUND, "Time point not valid"));

        this.mockMvc.perform(get("/api/v1/events/state?time=1"))
                .andDo(print()).andExpect(status().isNotFound()).andExpect(content().string("{\"status\":\"NOT_FOUND\"" +
                        ",\"message\":\"Time point not valid\"}"));
    }


    //FROM HERE EXCEPTION HANDLING TESTS

    @Test
    @WithMockUser
    public void whenHttpRequestMethodNotSupported_thenMethodNotAllowed() throws Exception {
        when(mallobCommands.getSystemState("1")).thenThrow(new FallobException(HttpStatus.NOT_FOUND, "Time point not valid"));

        this.mockMvc.perform(post("/api/v1/events/state?time=1"))
                .andDo(print()).andExpect(status().isMethodNotAllowed()).andExpect(content().string("{\"status\":" +
                        "\"METHOD_NOT_ALLOWED\",\"message\":\"Request method 'POST' not supported\\nPOST method is not supported for this request. " +
                        "Supported methods are GET \"}"));
    }

//    @Test
//    @WithMockUser
//    public void whenSendInvalidHttpMediaType_thenUnsupportedMediaType() throws Exception {
//        when(mallobCommands.getSystemState("1")).thenThrow(new FallobException(HttpStatus.NOT_FOUND, "Time point not valid"));
//
//        this.mockMvc.perform(get("/api/v1/events/state?time=1/1"))
//                .andDo(print()).andExpect(status().isNotFound()).andExpect(content().string("{\"status\":\"NOT_FOUND\"" +
//                        ",\"message\":\"Time point not valid\"}"));
//    }
//
//    @Test
//    @WithMockUser
//    public void whenNoHandlerForHttpRequest_thenNotFound() throws Exception {
//        when(mallobCommands.getSystemState("1")).thenThrow(new FallobException(HttpStatus.NOT_FOUND, "Time point not valid"));
//
//        this.mockMvc.perform(get("/api/v1/events/state/1"))
//                .andDo(print()).andExpect(status().isNotFound()).andExpect(content().string("{\"status\":\"NOT_FOUND\"" +
//                        ",\"message\":\"Time point not valid\"}"));
//    }
//
//    @Test
//    @WithMockUser
//    public void whenMethodArgumentMismatch_thenBadRequest() throws Exception {
//        this.mockMvc.perform(post("/api/v1/jobs/cancel/multiple"))
//                .andDo(print()).andExpect(status().isNotFound()).andExpect(content().string("{\"status\":\"NOT_FOUND\"" +
//                        ",\"message\":\"Time point not valid\"}"));
//    }


}
