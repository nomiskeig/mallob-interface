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

    private static final String REGISTER_UNSUCCESSFUL = "Registering was unsuccessful";

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
    private void authenticate(String username, String password) throws FallobException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new FallobException(HttpStatus.BAD_REQUEST, "USER_DISABLED");
        } catch (BadCredentialsException e) {
            throw new FallobException(HttpStatus.BAD_REQUEST, "INVALID_CREDENTIALS");
        }
    }

}
