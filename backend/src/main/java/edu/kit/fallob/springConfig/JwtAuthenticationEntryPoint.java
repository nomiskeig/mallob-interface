package edu.kit.fallob.springConfig;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A class used to print Errors in case of unauthorized requests
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable, AccessDeniedHandler {

    private static final long serialVersionUID = -7858869558953243875L;

    private static final String MESSAGE_BEGINNING = "\"message\":";
    private static final String STATUS_BEGINNING = "\"status\":";
    private static final String STATUS_401 = STATUS_BEGINNING + "\"UNAUTHORIZED\"";
    private static final String STATUS_403 = STATUS_BEGINNING + "\"FORBIDDEN\"";

    private static final String NOT_ADMIN_MESSAGE = MESSAGE_BEGINNING + "\"User is not an admin, access denied.\"";

    private static final int FORBIDDEN_CODE = 403;

    private static final int UNAUTHORIZED_CODE = 401;

    /**
     * This method will be executed by Spring (through the JwtRequestFilter) when an unauthorized user sends a request
     * Sends a HttpStatus and a message as response
     * @param request a servlet request
     * @param response a response that contains the HttpStatus and message
     * @param authException an Exception in case of an unauthorized user using the API
     * @throws IOException when the stream could not be initialized
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(UNAUTHORIZED_CODE);
        String message = MESSAGE_BEGINNING + "\"" +authException.getMessage() + "\"";
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print("{" + STATUS_401 + "," + message + "}");
        out.flush();
    }

    /**
     * This method will be executed by Spring (through the Security filter chain) when a non Admin sends a request to an API-Endpoint,
     * availbale only for Admins. The Method sends a HttpStatus and a message as response
     * @param request a servlet request
     * @param response a response that contains the HttpStatus and message
     * @param accessDeniedException an Exception in case of a non-Admin user using API-Endpoints available only for admins
     * @throws IOException when the stream could not be initialized
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(FORBIDDEN_CODE);
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print("{" + STATUS_403 + "," + NOT_ADMIN_MESSAGE + "}");
        out.flush();
    }
}