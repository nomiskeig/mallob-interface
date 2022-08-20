package edu.kit.fallob.springConfig;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -7858869558953243875L;

    private static final String STATUS_401 = "401 Unauthorized";
    private static final String STATUS_403 = "403 Forbidden";

    private static final String NOT_VERIFIED_OR_CORRUPT_TOKEN = "User not verified or corrupt token";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String message = "Message: ";
        String status = "Status: ";

        if (response.getStatus() == 403) {
            status += STATUS_403;
            message += NOT_VERIFIED_OR_CORRUPT_TOKEN;

        }
        else {
            status += STATUS_401;
            response.setStatus(401);
            message += authException.getMessage();
        }
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(status + "\n" + message);
        out.flush();
    }
}