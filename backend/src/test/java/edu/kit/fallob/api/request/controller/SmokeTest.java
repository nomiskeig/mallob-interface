package edu.kit.fallob.api.request.controller;


import static org.assertj.core.api.Assertions.assertThat;

import edu.kit.fallob.commands.*;
import edu.kit.fallob.springConfig.JwtAuthenticationEntryPoint;
import edu.kit.fallob.springConfig.JwtTokenUtil;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;

@SpringBootTest
    public class SmokeTest {

        @Autowired
        private AbortJobController abortJobController;
        @Autowired
        private FallobConfigurationController fallobConfigurationController;
        @Autowired
        private JobInformationController jobInformationController;
        @Autowired
        private JobSubmitController jobSubmitController;
        @Autowired
        private MallobEventsController mallobEventsController;
        @Autowired
        private MallobStartStopController mallobStartStopController;

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

        @Test
        public void contextLoads() throws Exception {
            assertThat(abortJobController).isNotNull();
            assertThat(fallobConfigurationController).isNotNull();
            assertThat(jobInformationController).isNotNull();
            assertThat(jobSubmitController).isNotNull();
            assertThat(mallobEventsController).isNotNull();
            assertThat(mallobStartStopController).isNotNull();
        }
    }
