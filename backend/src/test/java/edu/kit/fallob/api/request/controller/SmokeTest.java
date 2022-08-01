package edu.kit.fallob.api.request.controller;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
