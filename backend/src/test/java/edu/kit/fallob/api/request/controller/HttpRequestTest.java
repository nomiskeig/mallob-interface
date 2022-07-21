package edu.kit.fallob.api.request.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

//    @Test
//    public void registerTest() throws Exception {
//        assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/api/v1/users/register",
//                new UserRequest("kaloenev", "1234", "usoia@student.kit.edu"), String.class)).contains("\"status\":\"OK\"");
//    }
}
