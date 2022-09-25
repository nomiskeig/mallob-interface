package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.FallobCommands;
import edu.kit.fallob.springConfig.FallobException;
import edu.kit.fallob.springConfig.FallobWarning;
import edu.kit.fallob.springConfig.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A Rest Controller that handles user related requests like registration and authentication
 */
@RestController
@CrossOrigin
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private FallobCommands fallobCommand;

    private static final String REGISTER_UNSUCCESSFUL = "Registering was unsuccessful.";

    /**
     * A POST endpoint for registering in the Fallob system
     * Takes a request, parses the data needed to register in Fallob and forwards it. It is also responsible for system error handling
     * @param request a UserRequest object that contains an Email, a username and a password
     * @return sends a response stating OK if successful or an error (including a status code and a message in json format)
     */
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody UserRequest request) {
        boolean successful;
        try {
            successful = fallobCommand.register(request.getUsername(), request.getPassword(), request.getEmail());
        } catch (FallobException exception) {
            exception.printStackTrace();
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        } catch (NullPointerException exception) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        if (!successful) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(REGISTER_UNSUCCESSFUL);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /**
     * A POST endpoint for registering in the Fallob system
     * Takes a request, parses the data needed to register in Fallob and forwards it. It is also responsible for system error handling
     * @param authenticationRequest a UserRequest object that contains a username and a password
     * @return sends a response containing the jwtToken if successful or an error (including a status code and a message in json format)
     */
    @PostMapping("/login")
    public ResponseEntity<Object> createAuthenticationToken(@RequestBody UserRequest authenticationRequest) {

        try {
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        } catch(FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        final UserDetails userDetails;
        try {
             userDetails = fallobCommand.loadUserByUsername(authenticationRequest.getUsername());
        } catch (UsernameNotFoundException exception) {
            FallobWarning warning = new FallobWarning(HttpStatus.NOT_FOUND, exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    /**
     * A helper Method that calls Spring's AuthenticationManager and catches the errors from it
     * @param username the provided username by the user
     * @param password the provided password by the user
     * @throws FallobException an Exception summarizing the errors from the AuthenticationManager (standard Exception used in the system)
     */
    private void authenticate(String username, String password) throws FallobException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            e.printStackTrace();
            throw new FallobException(HttpStatus.BAD_REQUEST, "USER_DISABLED");
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            throw new FallobException(HttpStatus.BAD_REQUEST, "The provided credentials were invalid");
        }
    }

}
