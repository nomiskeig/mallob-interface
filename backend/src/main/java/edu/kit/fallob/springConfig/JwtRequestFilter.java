package edu.kit.fallob.springConfig;

import edu.kit.fallob.commands.FallobCommands;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A class used to authorize the users using their jwtTokens (uses Spring's security filter chain)
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private FallobCommands jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final String AUTHORIZATION = "Authorization";

    private static final String BEARER = "Bearer ";

    private static final String CAN_NOT_GET_JWT = "Unable to get JWT Token";

    private static final String JWT_EXPIRED = "JWT Token has expired";

    private static final String BEARER_WARNING = "JWT Token does not begin with Bearer String";

    private static final String USERNAME = "username";

    private static final String AUTHORITY = "authority";

    private static final int INDEX_BEARER = 7;

    /**
     * Used to authorize the users using their jwtTokens, accesses the Command class to get the user loaded in UserDetails object
     * and also the JwtTokenUtil to validate the token. Sets up the SecurityContextHolder of Spring if the token is valid
     * @param request a servlet request that can be set to carry additional information in the Controllers (carries only username at the moment)
     * @param response a servlet response that can be set to carry additional information in the Controllers
     * @param chain Spring Security's filter chain
     * @throws ServletException an exception in the request/response
     * @throws IOException an input/output exception
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader(AUTHORIZATION);

        String username = null;
        String jwtToken = null;
        // JWT Token is in the form "Bearer token". Remove Bearer word and get
        // only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith(BEARER)) {
            jwtToken = requestTokenHeader.substring(INDEX_BEARER);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println(CAN_NOT_GET_JWT);
            } catch (ExpiredJwtException e) {
                System.out.println(JWT_EXPIRED);
            }
        } else if (!request.getRequestURI().endsWith("register") && !request.getRequestURI().endsWith("login")) {
            logger.warn(BEARER_WARNING);
        }

        // Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = null;
            try {
                userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
            } catch (UsernameNotFoundException exception) {
                logger.error(exception.getMessage());
            }

            // if token is valid configure Spring Security to manually set authentication
            if (userDetails != null && jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the
                // Spring Security Configurations successfully.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                request.setAttribute(USERNAME, username);
                request.setAttribute(AUTHORITY, userDetails.getAuthorities().toString());
            }
        }
        chain.doFilter(request, response);
    }

}
