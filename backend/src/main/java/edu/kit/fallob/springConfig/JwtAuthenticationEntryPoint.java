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

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable, AccessDeniedHandler {

    private static final long serialVersionUID = -7858869558953243875L;

    private static final String MESSAGE_BEGINNING = "\"Message\":";
    private static final String STATUS_BEGINNING = "\"Status\":";
    private static final String STATUS_401 = STATUS_BEGINNING + "\"401 Unauthorized\"";
    private static final String STATUS_403 = STATUS_BEGINNING + "\"403 Forbidden\"";

    private static final String NOT_VERIFIED_OR_CORRUPT_TOKEN = MESSAGE_BEGINNING + "\"User is not verified or the token is corrupt.\"";

    private static final String NOT_ADMIN_MESSAGE = MESSAGE_BEGINNING + "\"User is not an admin, access denied.\"";

    private static final int FORBIDDEN_CODE = 403;

    private static final int UNAUTHORIZED_CODE = 401;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String status;
        String message;
        if (response.getStatus() == FORBIDDEN_CODE) {
            status = STATUS_403;
            message = NOT_VERIFIED_OR_CORRUPT_TOKEN;

        }
        else {
            response.setStatus(UNAUTHORIZED_CODE);
            status = STATUS_401;
            message = MESSAGE_BEGINNING + authException.getMessage();
        }
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print("{" + status + "," + message + "}");
        out.flush();
    }

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
