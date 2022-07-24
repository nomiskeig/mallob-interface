package edu.kit.fallob.springConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jayway.restassured.parsing.Parser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
public class RestExceptionHandlingTest {
//    private static final String URL_PREFIX = "http://localhost:8080";
//    // private FormAuthConfig formConfig = new FormAuthConfig(URL_PREFIX + "/login", "temporary", "temporary");
//
//    private String cookie;
//
//    private RequestSpecification givenAuth() {
//        return RestAssured.given()
//                .auth().preemptive()
//                .basic("user", "userPass");
//    }
//
//    @Test
//    public void whenTry_thenOK() {
//        final Response response = givenAuth().get(URL_PREFIX + "/api/foos");
//        assertEquals(200, response.statusCode());
//        System.out.println(response.asString());
//
//    }
//
//    @Test
//    public void whenMethodArgumentMismatch_thenBadRequest() {
//        RestAssured.defaultParser = Parser.JSON;
//        final Response response = givenAuth().get(URL_PREFIX + "/api/foos/ccc");
//        final ApiError error = response.as(ApiError.class);
//        String[] errors = error.getMessage().split("\n");
//        assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
//        // the first line being the message, the second the error
//        assertEquals(2, errors.length);
//        assertTrue(errors[1].contains("should be of type"));
//    }
//
//    @Test
//    public void whenNoHandlerForHttpRequest_thenNotFound() {
//        RestAssured.defaultParser = Parser.JSON;
//        final Response response = givenAuth().delete(URL_PREFIX + "/api/xx");
//        final ApiError error = response.as(ApiError.class);
//        String[] errors = error.getMessage().split("\n");
//        assertEquals(HttpStatus.NOT_FOUND, error.getStatus());
//        assertEquals(2, errors.length);
//        assertTrue(errors[1].contains("No handler found"));
//        System.out.println(response.asString());
//
//    }
//
//    @Test
//    public void whenHttpRequestMethodNotSupported_thenMethodNotAllowed() {
//        RestAssured.defaultParser = Parser.JSON;
//        final Response response = givenAuth().delete(URL_PREFIX + "/api/foos/1");
//        final ApiError error = response.as(ApiError.class);
//        String[] errors = error.getMessage().split("\n");
//        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, error.getStatus());
//        assertEquals(2, errors.length);
//        assertTrue(errors[1].contains("Supported methods are"));
//        System.out.println(response.asString());
//    }
//
//    @Test
//    public void whenSendInvalidHttpMediaType_thenUnsupportedMediaType() {
//        RestAssured.defaultParser = Parser.JSON;
//        final Response response = givenAuth().body("").post(URL_PREFIX + "/api/foos");
//        final ApiError error = response.as(ApiError.class);
//        String[] errors = error.getMessage().split("\n");
//        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, error.getStatus());
//        assertEquals(2, errors.length);
//        assertTrue(errors[1].contains("media type is not supported"));
//        System.out.println(response.asString());
//
//    }
}

