package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.BackendApplication;
import edu.kit.fallob.commands.*;
import edu.kit.fallob.springConfig.JwtAuthenticationEntryPoint;
import edu.kit.fallob.springConfig.JwtRequestFilter;
import edu.kit.fallob.springConfig.JwtTokenUtil;
import edu.kit.fallob.springConfig.WebSecurityConfig;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BackendApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpTests {

    @LocalServerPort
    private int port;

    private static HttpHeaders headers;

    private static TestRestTemplate restTemplate;

    private static JSONObject personJsonObject;

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

    @Autowired
    private JwtRequestFilter requestFilter;

    @Autowired
    private WebSecurityConfig webSecurityConfig;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private static UserDetails userDetails;

    private static HttpEntity<String> request;

    private static String FORBIDDEN_MESSAGE = "Status: 403 Forbidden\nMessage: User is not an admin, access denied";


    @BeforeAll
    public static void runBeforeAllTestMethods() throws JSONException {
        restTemplate = new TestRestTemplate();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        personJsonObject = new JSONObject();

        personJsonObject.put("username", "kalo");
        personJsonObject.put("password", "123");
        request = new HttpEntity<>(personJsonObject.toString(), headers);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("NORMAL_USER"));
        authorities.add(new SimpleGrantedAuthority("Verified"));
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode("123");
        userDetails = new User("kalo", encodedPassword, authorities);
    }

    @Test
    public void testUserUnauthorized() {
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                createURLWithPort("/api/v1/jobs/info/global"), HttpMethod.GET, request, String.class);
        String response = responseEntity.getBody();
        Assertions.assertEquals("Status: 401 Unauthorized\nMessage: Full authentication is required to access this resource", response);
    }

    @Test
    public void testAccessDenied() {

        when(fallobCommands.loadUserByUsername("kalo")).thenReturn(userDetails);

        String token = restTemplate.postForObject(
                createURLWithPort("/api/v1/users/login"),
                request, String.class);
        String jwtToken = token.substring(10, token.length() - 2);

        HttpHeaders headers2 = new HttpHeaders();

        headers2.add("Authorization", "Bearer " + jwtToken);
        HttpEntity<String> request2 = new HttpEntity<>(headers2);
        JwtTokenUtil jwtTokenUtil = Mockito.mock(JwtTokenUtil.class);
        when(jwtTokenUtil.validateToken(any(), any())).thenReturn(true);
        when(jwtTokenUtil.getUsernameFromToken(any())).thenReturn("kalo");

        String response = restTemplate.postForObject(
                createURLWithPort("/api/v1/system/mallob/restart"),
                request2, String.class);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                createURLWithPort("/api/v1/jobs/info/global"), HttpMethod.GET, request2, String.class);
        String response2 = responseEntity.getBody();

        ResponseEntity<String> responseEntity2 = restTemplate.exchange(
                createURLWithPort("/api/v1/system/mallobInfo"), HttpMethod.GET, request2, String.class);
        String response3 = responseEntity2.getBody();

        String response4 = restTemplate.postForObject(
                createURLWithPort("/api/v1/system/mallob/start"),
                request2, String.class);

        String response5 = restTemplate.postForObject(
                createURLWithPort("/api/v1/jobs/cancel/global"),
                request2, String.class);

        String response6 = restTemplate.postForObject(
                createURLWithPort("/api/v1/system/mallob/stop"),
                request2, String.class);

        Assertions.assertEquals(response, FORBIDDEN_MESSAGE);
        Assertions.assertEquals(response2, FORBIDDEN_MESSAGE);
        Assertions.assertEquals(response3, FORBIDDEN_MESSAGE);
        Assertions.assertEquals(response4, FORBIDDEN_MESSAGE);
        Assertions.assertEquals(response5, FORBIDDEN_MESSAGE);
        Assertions.assertEquals(response6, FORBIDDEN_MESSAGE);
    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
