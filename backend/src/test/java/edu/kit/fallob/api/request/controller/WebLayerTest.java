package edu.kit.fallob.api.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.fallob.api.request.stream.EventStreamStarter;
import edu.kit.fallob.api.request.stream.StreamInitializer;
import edu.kit.fallob.commands.*;
import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.dataobjects.*;
import edu.kit.fallob.mallobio.outputupdates.Event;
import edu.kit.fallob.mallobio.outputupdates.Warning;
import edu.kit.fallob.springConfig.FallobException;
import edu.kit.fallob.springConfig.JwtAuthenticationEntryPoint;
import edu.kit.fallob.springConfig.JwtTokenUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
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
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class WebLayerTest {
    private static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

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

    private static ResultMetaData result;

    private static JobInformation jobInformation;

    private static final String DESCRIPTION_CONTENT = "Here would usually be the Literals in cnf format";

    private static final String SOLUTION_CONTENT = "Here would usually be the solution in text format";

    private static final String NOT_FOUND = "Job not found";

    private static final String FILE_NAME = "description.cnf";

    private static final String FILE_NAME2 = "description2.cnf";

    private static final String FILE_RESULT = "solution.txt";

    private static final String FILE_RESULT2 = "solution2.txt";

    private static final String NOT_FOUND_MULTIPLE = "Jobs not found";

    private static final String NOT_FOUND_EXCEPTION = "{\"status\":\"NOT_FOUND\",\"message\":\"" + NOT_FOUND + "\"}";
    private static final String NOT_FOUND_EXCEPTION_MULTIPLE = "{\"status\":\"NOT_FOUND\",\"message\":\"" + NOT_FOUND_MULTIPLE + "\"}";

    private static final String USER_DOES_NOT_OWN_JOB = "Job does not belong to this user";

    private static final String LOG_LINE_PLACE = "Here would be a log line";

    private static final String JSON_DOES_NOT_OWN_JOB = "{\"status\":\"FORBIDDEN\",\"message\":\"" + USER_DOES_NOT_OWN_JOB + "\"}";

    private static final String JSON_JOB_INFORMATION = "{\"config\":{\"name\":\"Job1\",\"priority\":1.0," +
            "\"application\":\"SAT\",\"maxDemand\":1,\"cpuLimit\":\"1.0\",\"arrival\":\"bspArrival\"," +
            "\"dependencies\":[1,2],\"contentMode\":\"text\",\"incremental\":true,\"precursor\":1," +
            "\"assumptions\":\"assumptions\",\"wallclockLimit\":\"1.0\"}," +
            "\"resultData\":{\"parsingTime\":1.0,\"processingTime\":1.0,\"schedulingTime\":1.0,\"totalTime\":1.0," +
            "\"usedCpuSeconds\":1.0,\"usedWallclockSeconds\":1.0},\"email\":\"kalo@student.kit.edu\"," +
            "\"user\":\"kalo\",\"submitTime\":\"12:34:32\",\"status\":\"DONE\",\"jobID\":1}";
    private static final String JSON_MULTIPLE_JOB_INFORMATION = "{\"information\":[" + JSON_JOB_INFORMATION + "]}";

    private static final LocalDateTime TIME_BETWEEN = LocalDateTime.of(2020, 2, 13, 18, 52, 9);

    private static final ZonedDateTime TIME_WITH_ZONE = TIME_BETWEEN.atZone(ZoneOffset.UTC);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);

    private static final String JSON_EVENTS = "{\"events\":[{" +
            "\"rank\":1,\"treeIndex\":1,\"jobID\":1,\"load\":1,\"time\":\"" + TIME_WITH_ZONE.format(FORMATTER) + "\"}]}";

    private static final String JOB_ID_JSON = "{\"jobID\":1}";

    private static final String JOB_IDS_JSON = "{\"cancelled\":[1,2]}";
    private static final String OK_JSON = "\"OK\"";

    private static final String EMAIL = "kalo@student.kit.edu";
    private static final String USERNAME = "kalo";
    private static final String PASSWORD = "1234";

    private static final String PARAMS = "Here would be parameters";

    private static final String TIME_POINT_NOT_VALID = "Time point not valid";

    private static final String AUTHORITY_ADMIN = "ADMIN";

    private static final String NO_JOBS_ACTIVE = "No jobs are active";

    private static final String JSON_NO_JOBS_ACTIVE = "{\"status\":\"CONFLICT\",\"message\":\"" + NO_JOBS_ACTIVE + "\"}";

    private static FallobConfiguration fallobConfiguration;

    private static SubmitJobRequest submitJobRequest;


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
        String jobName = "Job1";
        submitJobRequest = new SubmitJobRequest();
        submitJobRequest.setApplication("SAT");
        submitJobRequest.setPriority(1.0);
        submitJobRequest.setName(jobName);
        submitJobRequest.setDescriptionID(1);
        submitJobRequest.setMaxDemand(1);
        submitJobRequest.setIncremental(true);
        submitJobRequest.setDependencies(dependencies);
        submitJobRequest.setWallClockLimit(String.valueOf(1.0));
        submitJobRequest.setCpuLimit(String.valueOf(1.0));
        submitJobRequest.setArrival("bspArrival");
        submitJobRequest.setDone(false);
        submitJobRequest.setContentMode("text");
        submitJobRequest.setPrecursor(1);
        submitJobRequest.setAssumptions("assumptions");
        jobConfig = submitJobRequest.getJobConfiguration();
        User user = new NormalUser(USERNAME, PASSWORD, EMAIL);
        result = new ResultMetaData(1, 1, 1, 1, 1, 1);
        jobInformation = new JobInformation(WebLayerTest.jobConfig, result, user, "12:34:32", JobStatus.DONE, 1);
        fallobConfiguration = mock(FallobConfiguration.class);
        try {
            Field instance = FallobConfiguration.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(instance, fallobConfiguration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    public static void afterAll() throws NoSuchFieldException, IllegalAccessException {
        Field instance = FallobConfiguration.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    @WithMockUser
    public void saveDescriptionSuccessfully() throws Exception {
        File file = new File(FILE_NAME);
        FileWriter myWriter = new FileWriter(file);
        myWriter.write(DESCRIPTION_CONTENT);
        myWriter.close();
        JobDescription jobDescription = new JobDescription(Collections.singletonList(file), SubmitType.EXCLUSIVE);
        MockMultipartFile multipartFile = new MockMultipartFile("file", FILE_NAME,
                MediaType.TEXT_PLAIN_VALUE, DESCRIPTION_CONTENT.getBytes());
        when(fallobConfiguration.getDescriptionsbasePath()).thenReturn("./fallob-data/descriptions");

        when(jobSubmitCommands.saveJobDescription(null, jobDescription)).thenReturn(1);

        this.mockMvc.perform(multipart("/api/v1/jobs/submit/exclusive/description").file(multipartFile)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"descriptionID\":0}"));
    }


    @Test
    @WithMockUser
    public void saveEmptyDescriptionFile() throws Exception {
        File file = new File(FILE_NAME);
        JobDescription jobDescription = new JobDescription(Collections.singletonList(file), SubmitType.EXCLUSIVE);
        MockMultipartFile multipartFile = new MockMultipartFile("file", FILE_NAME,
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
        File file = new File("jobDescription.cnf");
        FileWriter myWriter = new FileWriter(file);
        for (String description : descriptionsList) {
            myWriter.write(description);
        }
        myWriter.close();
        List<File> filesList = new ArrayList<>();
        filesList.add(file);
        JobDescription jobDescription = new JobDescription(filesList, SubmitType.INCLUSIVE);
        when(fallobConfiguration.getDescriptionsbasePath()).thenReturn("./fallob-data/descriptions");

        when(jobSubmitCommands.submitJobWithDescriptionInclusive(isNull(), any(JobDescription.class), any(JobConfiguration.class)))
                .thenAnswer((Answer<Integer>) invocationOnMock -> {
                    JobDescription usedJobDescription = invocationOnMock.getArgument(1);
                    JobConfiguration usedConfig = invocationOnMock.getArgument(2);
                    boolean jobConfigEqualsUsedConfig = usedConfig.isDone() == jobConfig.isDone()
                            && usedConfig.isIncremental() == jobConfig.isIncremental() && usedConfig.isInterrupt()
                            == jobConfig.isInterrupt() && Objects.equals((usedConfig.getAdditionalParameter()), jobConfig.getAdditionalParameter())
                            && usedConfig.getApplication().equals(jobConfig.getApplication())
                            && Arrays.equals((usedConfig.getDependencies()), jobConfig.getDependencies())
                            && usedConfig.getPrecursor() == jobConfig.getPrecursor() && usedConfig.getPriority() == jobConfig.getPriority()
                            && usedConfig.getDescriptionID() == jobConfig.getDescriptionID() && usedConfig.getName().equals(jobConfig.getName())
                            && Arrays.equals((usedConfig.getLiterals()), jobConfig.getLiterals())
                            && Objects.equals(usedConfig.getWallClockLimit(), jobConfig.getWallClockLimit())
                            && usedConfig.getMaxDemand() == jobConfig.getMaxDemand();

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

        submitJobRequest.setDescription(descriptionsList);

        this.mockMvc.perform(post("/api/v1/jobs/submit/inclusive").content(objectMapper.writeValueAsString(submitJobRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(JOB_ID_JSON));
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
                            && Arrays.equals((usedConfig.getDependencies()), jobConfig.getDependencies())
                            && usedConfig.getPrecursor() == jobConfig.getPrecursor() && usedConfig.getPriority() == jobConfig.getPriority()
                            && usedConfig.getDescriptionID() == jobConfig.getDescriptionID() && usedConfig.getName().equals(jobConfig.getName())
                            && Arrays.equals((usedConfig.getLiterals()), jobConfig.getLiterals())
                            && Objects.equals(usedConfig.getWallClockLimit(), jobConfig.getWallClockLimit())
                            && usedConfig.getMaxDemand() == jobConfig.getMaxDemand();

                    if (descriptionId == jobConfig.getDescriptionID() && jobConfigEqualsUsedConfig) {
                        return 1;
                    } else {
                        return -1;
                    }
                });

        this.mockMvc.perform(post("/api/v1/jobs/submit/exclusive/config")
                        .content(objectMapper.writeValueAsString(submitJobRequest)).contentType("application/json"))
                .andDo(print()).andExpect(status().isOk()).andExpect(content().string(JOB_ID_JSON));
    }

    @Test
    @WithMockUser
    public void submitJobWithUrl() throws Exception {
        File file = new File(FILE_NAME);
        FileWriter myWriter = new FileWriter(file);
        myWriter.write(DESCRIPTION_CONTENT);
        myWriter.close();
        String url = file.toURI().toURL().toString();
        submitJobRequest.setUrl(url);

        JobDescription jobDescription = new JobDescription(Collections.singletonList(file), SubmitType.INCLUSIVE);
        when(fallobConfiguration.getDescriptionsbasePath()).thenReturn("./fallob-data/descriptions");

        when(jobSubmitCommands.submitJobWithDescriptionInclusive(isNull(), any(JobDescription.class), any(JobConfiguration.class)))
                .thenAnswer((Answer<Integer>) invocationOnMock -> {
                    JobDescription usedJobDescription = invocationOnMock.getArgument(1);
                    JobConfiguration usedConfig = invocationOnMock.getArgument(2);
                    boolean jobConfigEqualsUsedConfig = usedConfig.isDone() == jobConfig.isDone()
                            && usedConfig.isIncremental() == jobConfig.isIncremental() && usedConfig.isInterrupt()
                            == jobConfig.isInterrupt() && Objects.equals((usedConfig.getAdditionalParameter()), jobConfig.getAdditionalParameter())
                            && usedConfig.getApplication().equals(jobConfig.getApplication())
                            && Arrays.equals((usedConfig.getDependencies()), jobConfig.getDependencies())
                            && usedConfig.getPrecursor() == jobConfig.getPrecursor() && usedConfig.getPriority() == jobConfig.getPriority()
                            && usedConfig.getDescriptionID() == jobConfig.getDescriptionID() && usedConfig.getName().equals(jobConfig.getName())
                            && Arrays.equals((usedConfig.getLiterals()), jobConfig.getLiterals())
                            && Objects.equals(usedConfig.getWallClockLimit(), jobConfig.getWallClockLimit())
                            && usedConfig.getMaxDemand() == jobConfig.getMaxDemand();

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
                .andExpect(status().isOk()).andExpect(content().string(JOB_ID_JSON));
    }


    @Test
    public void registerSuccessfully() throws Exception {
        
        when(fallobCommands.register(USERNAME, PASSWORD, EMAIL)).thenReturn(true);
        UserRequest userRequest = new UserRequest(USERNAME, PASSWORD, EMAIL);

        this.mockMvc.perform(post("/api/v1/users/register").content(objectMapper.writeValueAsString(userRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(OK_JSON));
    }

    @Test
    public void registerException() throws Exception {
        String message = "Username already registered";
        when(fallobCommands.register(USERNAME, PASSWORD, EMAIL)).thenThrow(new FallobException(HttpStatus.CONFLICT, message));
        UserRequest userRequest = new UserRequest(USERNAME, PASSWORD, EMAIL);

        this.mockMvc.perform(post("/api/v1/users/register").content(objectMapper.writeValueAsString(userRequest))
                        .contentType("application/json")).andDo(print()).andExpect(status().isConflict())
                .andExpect(content().string("{\"status\":\"CONFLICT\",\"message\":" + "\"" + message + "\"}"));
    }

    @Test
    public void loginSuccessfully() throws Exception {

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(USERNAME, PASSWORD, Collections.singleton(UserType.NORMAL_USER));
        when(fallobCommands.loadUserByUsername(USERNAME)).thenReturn(userDetails);
        UserRequest userRequest = new UserRequest(USERNAME, PASSWORD);
        Authentication authentication = mock(Authentication.class);
        authentication.setAuthenticated(true);
        when(authentication.isAuthenticated()).thenReturn(true);
        String token = "1234";

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn(token);

        this.mockMvc.perform(post("/api/v1/users/login").content(objectMapper.writeValueAsString(userRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"token\":\"" + token + "\"}"));
    }

    @Test
    public void loginException() throws Exception {
        String message = "User not found";
        when(fallobCommands.loadUserByUsername(USERNAME)).thenThrow(new UsernameNotFoundException(message));
        UserRequest userRequest = new UserRequest(USERNAME, PASSWORD);
        Authentication authentication = mock(Authentication.class);
        authentication.setAuthenticated(true);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        this.mockMvc.perform(post("/api/v1/users/login").content(objectMapper.writeValueAsString(userRequest))
                        .contentType("application/json")).andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string("{\"status\":\"NOT_FOUND\",\"message\":" + "\"" + message + "\"}"));
    }


    @Test
    @WithMockUser
    public void abortSingleJobSuccessfully() throws Exception {
        when(jobAbortCommands.abortSingleJob(null, 1, false)).thenReturn(true);

        this.mockMvc.perform(post("/api/v1/jobs/cancel/single/{jobId}", 1)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(OK_JSON));
    }

    @Test
    @WithMockUser
    public void abortSingleJobException() throws Exception {
        when(jobAbortCommands.abortSingleJob(null, 1, false)).thenThrow(new FallobException(HttpStatus.FORBIDDEN, USER_DOES_NOT_OWN_JOB));

        this.mockMvc.perform(post("/api/v1/jobs/cancel/single/{jobId}", 1)).andDo(print())
                .andExpect(status().isForbidden()).andExpect(content().string(JSON_DOES_NOT_OWN_JOB));
    }

    @Test
    @WithMockUser
    public void abortSingleJobUnsuccessfully() throws Exception {
        when(jobAbortCommands.abortSingleJob(null, 1, false)).thenReturn(false);

        this.mockMvc.perform(post("/api/v1/jobs/cancel/single/{jobId}", 1)).andDo(print())
                .andExpect(status().isConflict()).andExpect(content().string("Job is not active"));
    }

    @Test
    @WithMockUser
    public void abortMultipleJobsSuccessfully() throws Exception {

        when(jobAbortCommands.abortMultipleJobs(null, jobIds)).thenReturn(jobIdsList);

        AbortJobRequest abortJobRequest = new AbortJobRequest(jobIds);
        this.mockMvc.perform(post("/api/v1/jobs/cancel").content(objectMapper.writeValueAsString(abortJobRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(JOB_IDS_JSON));
    }

    @Test
    @WithMockUser
    public void abortMultipleJobsException() throws Exception {
        when(jobAbortCommands.abortMultipleJobs(null, jobIds)).thenThrow(new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND_MULTIPLE));

        AbortJobRequest abortJobRequest = new AbortJobRequest(jobIds);
        this.mockMvc.perform(post("/api/v1/jobs/cancel").content(objectMapper.writeValueAsString(abortJobRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isNotFound()).andExpect(content().string(NOT_FOUND_EXCEPTION_MULTIPLE));
    }

    @Test
    @WithMockUser
    public void abortMultipleJobsUnsuccessfully() throws Exception {
        when(jobAbortCommands.abortMultipleJobs(null, jobIds)).thenReturn(new ArrayList<>());

        AbortJobRequest abortJobRequest = new AbortJobRequest(jobIds);
        this.mockMvc.perform(post("/api/v1/jobs/cancel").content(objectMapper.writeValueAsString(abortJobRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isConflict()).andExpect(content().string(NO_JOBS_ACTIVE));
    }

    @Test
    @WithMockUser
    public void abortAllJobsSuccessfully() throws Exception {

        when(jobAbortCommands.abortAllJobs(null)).thenReturn(jobIdsList);

        this.mockMvc.perform(post("/api/v1/jobs/cancel/all")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(JOB_IDS_JSON));
    }

    @Test
    @WithMockUser
    public void abortAllJobsException() throws Exception {

        when(jobAbortCommands.abortAllJobs(null)).thenThrow(new FallobException(HttpStatus.CONFLICT, NO_JOBS_ACTIVE));

        this.mockMvc.perform(post("/api/v1/jobs/cancel/all")).andDo(print())
                .andExpect(status().isConflict()).andExpect(content().string(JSON_NO_JOBS_ACTIVE));
    }

    @Test
    @WithMockUser(authorities = AUTHORITY_ADMIN)
    public void abortJobsGlobalSuccessfully() throws Exception {
        when(jobAbortCommands.abortAllGlobalJob(null)).thenReturn(jobIdsList);

        this.mockMvc.perform(post("/api/v1/jobs/cancel/global")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(JOB_IDS_JSON));
    }

    @Test
    @WithMockUser(authorities = AUTHORITY_ADMIN)
    public void abortJobsGlobalException() throws Exception {
        when(jobAbortCommands.abortAllGlobalJob(null)).thenThrow(new FallobException(HttpStatus.CONFLICT, NO_JOBS_ACTIVE));

        this.mockMvc.perform(post("/api/v1/jobs/cancel/global")).andDo(print())
                .andExpect(status().isConflict()).andExpect(content().string(JSON_NO_JOBS_ACTIVE));
    }

    @Test
    @WithMockUser
    public void abortIncrementalJobSuccessfully() throws Exception {
        when(jobAbortCommands.abortSingleJob(null, 1, true)).thenReturn(true);

        this.mockMvc.perform(post("/api/v1/jobs/cancel/incremental/{jobId}", 1)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(OK_JSON));
    }

    @Test
    @WithMockUser
    public void abortIncrementalJobException() throws Exception {
        when(jobAbortCommands.abortSingleJob(null, 1, true)).thenThrow(new FallobException(HttpStatus.FORBIDDEN, USER_DOES_NOT_OWN_JOB));

        this.mockMvc.perform(post("/api/v1/jobs/cancel/incremental/{jobId}", 1)).andDo(print())
                .andExpect(status().isForbidden()).andExpect(content().string(JSON_DOES_NOT_OWN_JOB));
    }

    @Test
    @WithMockUser
    public void restartJobSuccessfully() throws Exception {
        when(jobSubmitCommands.restartCanceledJob(null, 1)).thenReturn(1);

        this.mockMvc.perform(post("/api/v1/jobs/submit/restart/{jobId}", 1)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(JOB_ID_JSON));
    }

    @Test
    @WithMockUser
    public void restartJobException() throws Exception {
        when(jobSubmitCommands.restartCanceledJob(null, 1)).thenThrow(new FallobException(HttpStatus.FORBIDDEN, USER_DOES_NOT_OWN_JOB));

        this.mockMvc.perform(post("/api/v1/jobs/submit/restart/{jobId}", 1)).andDo(print())
                .andExpect(status().isForbidden()).andExpect(content().string(JSON_DOES_NOT_OWN_JOB));
    }

    @Test
    @WithMockUser
    public void getFallobConfig() throws Exception {
        FallobConfiguration fallobConfiguration = Mockito.mock(FallobConfiguration.class);
        when(fallobConfiguration.getAmountProcesses()).thenReturn(1);
        when(fallobConfiguration.getStartTime()).thenReturn(LocalDateTime.parse("2020-04-13T17:53:12.840"));
        when(fallobConfiguration.getDefaultJobPriority()).thenReturn(1F);
        when(fallobConfiguration.getDefaultWallClockLimit()).thenReturn("1");
        when(fallobConfiguration.getDefaultContentMode()).thenReturn("content");

        when(fallobCommands.getFallobConfiguration()).thenReturn(fallobConfiguration);

        this.mockMvc.perform(get("/api/v1/system/config")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"amountProcesses\":1," +
                        "\"startTime\":\"2020-04-13T17:53:12.840Z\",\"defaults\":{\"priority\":1.0,\"wallClockLimit\":\"1\"," +
                        "\"contentMode\":\"content\"}}"));


    }

    @Test
    @WithMockUser
    public void getSingleJobInformationSuccessfully() throws Exception {
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
        List<JobInformation> jobInformationList = new ArrayList<>();
        jobInformationList.add(jobInformation);
        when(jobInformationCommands.getMultipleJobInformation(null, jobIds)).thenReturn(jobInformationList);

        JobInformationRequest abortJobRequest = new JobInformationRequest(jobIds);
        this.mockMvc.perform(post("/api/v1/jobs/info").content(objectMapper.writeValueAsString(abortJobRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(JSON_MULTIPLE_JOB_INFORMATION));
    }

    @Test
    @WithMockUser
    public void getMultipleJobInformationException() throws Exception {
        when(jobInformationCommands.getMultipleJobInformation(null, jobIds)).thenThrow(new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND_MULTIPLE));

        JobInformationRequest abortJobRequest = new JobInformationRequest(jobIds);
        this.mockMvc.perform(post("/api/v1/jobs/info").content(objectMapper.writeValueAsString(abortJobRequest))
                        .contentType("application/json")).andDo(print())
                .andExpect(status().isNotFound()).andExpect(content().string(NOT_FOUND_EXCEPTION_MULTIPLE));
    }

    @Test
    @WithMockUser
    public void getAllJobInformationSuccessfully() throws Exception {
        List<JobInformation> jobInformationList = new ArrayList<>();
        jobInformationList.add(jobInformation);
        when(jobInformationCommands.getAllJobInformation(null)).thenReturn(jobInformationList);

        this.mockMvc.perform(get("/api/v1/jobs/info/all")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(JSON_MULTIPLE_JOB_INFORMATION));
    }

    @Test
    @WithMockUser(authorities = AUTHORITY_ADMIN)
    public void getGlobalJobInformationSuccessfully() throws Exception {
        List<JobInformation> jobInformationList = new ArrayList<>();
        jobInformationList.add(jobInformation);
        when(jobInformationCommands.getAllGlobalJobInformation(null)).thenReturn(jobInformationList);

        this.mockMvc.perform(get("/api/v1/jobs/info/global")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(JSON_MULTIPLE_JOB_INFORMATION));
    }

    @Test
    @WithMockUser
    public void getSingleJobDescriptionString() throws Exception {
        File file = new File(FILE_NAME);
        FileWriter myWriter = new FileWriter(file);
        myWriter.write(DESCRIPTION_CONTENT);
        myWriter.close();
        JobDescription jobDescription = new JobDescription(Collections.singletonList(file), SubmitType.INCLUSIVE);
        when(jobDescriptionCommands.getSingleJobDescription(null, 1)).thenReturn(jobDescription);

        this.mockMvc.perform(get("/api/v1/jobs/description/single/{jobId}", 1)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"description\":[\"" + DESCRIPTION_CONTENT + "\"]}"));
    }

    @Test
    @WithMockUser
    public void getSingleJobDescriptionFile() throws Exception {
        File file = new File(FILE_NAME);
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
        File file = new File(FILE_NAME);
        File file2 = new File(FILE_NAME2);
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

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/jobs/description").content(objectMapper.writeValueAsString(jobDescriptionRequest))
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

        this.mockMvc.perform(post("/api/v1/jobs/description").content(objectMapper.writeValueAsString(jobDescriptionRequest))
                .contentType("application/json")).andDo(print()).andExpect(status().isNotFound()).andExpect(content()
                .string(NOT_FOUND_EXCEPTION_MULTIPLE));
    }

    @Test
    @WithMockUser
    public void getAllJobDescriptions() throws Exception {
        File file = new File(FILE_NAME);
        File file2 = new File(FILE_NAME2);
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
        File file = new File(FILE_RESULT);
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
        File file = new File(FILE_RESULT);
        File file2 = new File(FILE_RESULT2);
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

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/jobs/solution").content(objectMapper.writeValueAsString(jobResultRequest))
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

        this.mockMvc.perform(post("/api/v1/jobs/solution").content(objectMapper.writeValueAsString(jobResultRequest))
                .contentType("application/json")).andDo(print()).andExpect(status().isNotFound()).andExpect(content()
                .string(NOT_FOUND_EXCEPTION_MULTIPLE));
    }

    @Test
    @WithMockUser
    public void getAllJobResults() throws Exception {
        File file = new File(FILE_RESULT);
        File file2 = new File(FILE_RESULT2);
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
        when(jobPendingCommand.waitForJob(null, 1)).thenReturn(result);

        this.mockMvc.perform(get("/api/v1/jobs/waitFor/{jobId}", 1)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"resultMetaData\":{\"parsingTime\":1.0," +
                        "\"processingTime\":1.0,\"schedulingTime\":1.0,\"totalTime\":1.0,\"usedCpuSeconds\":1.0,\"usedWallclockSeconds\":1.0}}"));
    }

    @Test
    @WithMockUser
    public void waitForJobException() throws Exception {
        when(jobPendingCommand.waitForJob(null, 1)).thenThrow(new FallobException(HttpStatus.NOT_FOUND, NOT_FOUND));

        this.mockMvc.perform(get("/api/v1/jobs/waitFor/{jobId}", 1)).andDo(print())
                .andExpect(status().isNotFound()).andExpect(content().string(NOT_FOUND_EXCEPTION));
    }

    @Test
    @WithMockUser(authorities = AUTHORITY_ADMIN)
    public void getWarnings() throws Exception {
        LocalDateTime time = LocalDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        ZonedDateTime timeWithZone = time.atZone(ZoneOffset.UTC);

        Warning warning = new Warning(LOG_LINE_PLACE, time);
        when(mallobCommands.getWarnings()).thenReturn(Collections.singletonList(warning));

        this.mockMvc.perform(get("/api/v1/system/mallobInfo", 1)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"warnings\":[{\"timestamp\":\"" + timeWithZone.format(formatter) + "\",\"message\":\"" + LOG_LINE_PLACE + "\"}]}"));
    }

    @Test
    @WithMockUser(authorities = AUTHORITY_ADMIN)
    public void getWarningsException() throws Exception {
        String exceptionMessage = "SQL Exception";
    when(mallobCommands.getWarnings()).thenThrow(new FallobException(HttpStatus.INTERNAL_SERVER_ERROR, exceptionMessage));

        this.mockMvc.perform(get("/api/v1/system/mallobInfo", 1)).andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(content().string("{\"status\":\"INTERNAL_SERVER_ERROR\",\"message\":\"" + exceptionMessage + "\"}"));
    }

    @Test
    @WithMockUser(authorities = AUTHORITY_ADMIN)
    public void startMallobSuccessfully() throws Exception {

        when(mallobCommands.startMallob(PARAMS)).thenReturn(true);
        MallobStartStopRequest mallobStartStopRequest = new MallobStartStopRequest(PARAMS);

        this.mockMvc.perform(post("/api/v1/system/mallob/start").content(objectMapper.writeValueAsString(mallobStartStopRequest))
                .contentType("application/json")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(OK_JSON));
    }

    @Test
    @WithMockUser(authorities = AUTHORITY_ADMIN)
    public void startMallobUnsuccessfully() throws Exception {

        when(mallobCommands.startMallob(PARAMS)).thenReturn(false);
        MallobStartStopRequest mallobStartStopRequest = new MallobStartStopRequest(PARAMS);

        this.mockMvc.perform(post("/api/v1/system/mallob/start").content(objectMapper.writeValueAsString(mallobStartStopRequest))
                .contentType("application/json")).andDo(print()).andExpect(status().isConflict()).andExpect(content().string("The system is already running"));
    }

    @Test
    @WithMockUser(authorities = AUTHORITY_ADMIN)
    public void stopMallobSuccessfully() throws Exception {
        when(mallobCommands.stopMallob()).thenReturn(true);

        this.mockMvc.perform(post("/api/v1/system/mallob/stop")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(OK_JSON));
    }

    @Test
    @WithMockUser(authorities = AUTHORITY_ADMIN)
    public void stopMallobUnsuccessfully() throws Exception {
        when(mallobCommands.stopMallob()).thenReturn(false);

        this.mockMvc.perform(post("/api/v1/system/mallob/stop")).andDo(print()).andExpect(status().isConflict())
                .andExpect(content().string("The system is not running"));
    }

    @Test
    @WithMockUser(authorities = AUTHORITY_ADMIN)
    public void restartMallobSuccessfully() throws Exception {

        when(mallobCommands.stopMallob()).thenReturn(true);
        when(mallobCommands.startMallob(PARAMS)).thenReturn(true);
        MallobStartStopRequest mallobStartStopRequest = new MallobStartStopRequest(PARAMS);

        this.mockMvc.perform(post("/api/v1/system/mallob/restart").content(objectMapper.writeValueAsString(mallobStartStopRequest))
                .contentType("application/json")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(OK_JSON));
    }

    @Test
    @WithMockUser(authorities = AUTHORITY_ADMIN)
    public void restartMallobUnsuccessfully() throws Exception {

        when(mallobCommands.stopMallob()).thenReturn(false);
        when(mallobCommands.startMallob(PARAMS)).thenReturn(true);
        MallobStartStopRequest mallobStartStopRequest = new MallobStartStopRequest(PARAMS);

        this.mockMvc.perform(post("/api/v1/system/mallob/restart").content(objectMapper.writeValueAsString(mallobStartStopRequest))
                .contentType("application/json")).andDo(print()).andExpect(status().isConflict()).andExpect(content().string("The system is not running"));
    }


    @Test
    @WithMockUser
    public void getMallobEventsSuccessfully() throws Exception {
        String startTime = "2020-02-13T18:51:09.840";
        String endTime = "2020-02-13T18:54:09.234";
        List<Event> eventsList = new ArrayList<>();
        Event event = new Event(1, 1, 1, true, TIME_BETWEEN);
        eventsList.add(event);

        when(mallobCommands.getEvents(LocalDateTime.parse(startTime), LocalDateTime.parse(endTime))).thenReturn(eventsList);

        this.mockMvc.perform(get("/api/v1/events/events?startTime=" + startTime + "&endTime=" + endTime))
                .andDo(print()).andExpect(status().isOk()).andExpect(content().string(JSON_EVENTS));
    }

    @Test
    @WithMockUser
    public void getMallobEventsException() throws Exception {
        String startTime = "2020-02-13T18:51:09.840";
        String endTime = "2020-02-13T18:54:09.234";
        when(mallobCommands.getEvents(LocalDateTime.parse(startTime), LocalDateTime.parse(endTime)))
                .thenThrow(new FallobException(HttpStatus.NOT_FOUND, TIME_POINT_NOT_VALID));

        this.mockMvc.perform(get("/api/v1/events/events?startTime=" + startTime + "&endTime=" + endTime))
                .andDo(print()).andExpect(status().isNotFound()).andExpect(content().string("{\"status\":\"NOT_FOUND\"" +
                        ",\"message\":\"" + TIME_POINT_NOT_VALID + "\"}"));
    }

    @Test
    @WithMockUser
    public void getSystemStateSuccessfully() throws Exception {
        List<Event> eventsList = new ArrayList<>();
        Event event = new Event(1, 1, 1, true, TIME_BETWEEN);
        eventsList.add(event);
        SystemState systemState = new SystemState(LocalDateTime.now());
        systemState.setSystemState(eventsList);

        when(mallobCommands.getSystemState(LocalDateTime.parse(TIME_BETWEEN.toString()))).thenReturn(systemState);

        this.mockMvc.perform(get("/api/v1/events/state?time=" + TIME_BETWEEN))
                .andDo(print()).andExpect(status().isOk()).andExpect(content().string(JSON_EVENTS));
    }

    @Test
    @WithMockUser
    public void getSystemStateException() throws Exception {
        String time = "2020-02-13T18:51:09.840";
        when(mallobCommands.getSystemState(LocalDateTime.parse(time))).thenThrow(new FallobException(HttpStatus.NOT_FOUND, TIME_POINT_NOT_VALID));

        this.mockMvc.perform(get("/api/v1/events/state?time=" + time))
                .andDo(print()).andExpect(status().isNotFound()).andExpect(content().string("{\"status\":\"NOT_FOUND\"" +
                        ",\"message\":\"" + TIME_POINT_NOT_VALID + "\"}"));
    }

    @Test
    @WithMockUser
    public void getSystemStateBadRequest() throws Exception {
        this.mockMvc.perform(get("/api/v1/events/state?time=1"))
                .andDo(print()).andExpect(status().isBadRequest()).andExpect(content().string("{\"status\":\"BAD_REQUEST\"," +
                        "\"message\":\"Text '1' could not be parsed at index 0\"}"));
    }


    //FROM HERE EXCEPTION HANDLING TESTS

    @Test
    @WithMockUser
    public void whenHttpRequestMethodNotSupported_thenMethodNotAllowed() throws Exception {
        this.mockMvc.perform(post("/api/v1/events/state?time=1"))
                .andDo(print()).andExpect(status().isMethodNotAllowed()).andExpect(content().string("{\"status\":" +
                        "\"METHOD_NOT_ALLOWED\",\"message\":\"Request method 'POST' not supported\\nPOST method is not supported for this request. " +
                        "Supported methods are GET \"}"));
    }

    @Test
    @WithMockUser
    public void missingRequestParameterTest() throws Exception {
        this.mockMvc.perform(get("/api/v1/events/state?time"))
                .andDo(print()).andExpect(status().isBadRequest()).andExpect(content().string("{\"status\":\"BAD_REQUEST\"," +
                        "\"message\":\"Required request parameter 'time' for method parameter type String is not present\\ntime parameter is missing\"}"));
    }

    @Test
    @WithMockUser
    public void argumentMismatchTest() throws Exception {
        when(jobAbortCommands.abortSingleJob(null, 1, false)).thenReturn(true);

        this.mockMvc.perform(post("/api/v1/jobs/cancel/single/{jobId}", USERNAME)).andDo(print())
                .andExpect(status().isBadRequest()).andExpect(content().string("{\"status\":\"BAD_REQUEST\"," +
                        "\"message\":\"Failed to convert value of type 'java.lang.String' to required type 'int'; nested" +
                        " exception is java.lang.NumberFormatException: For input string: \\\"kalo\\\"\\njobId should be of type int\"}"));
    }



}
