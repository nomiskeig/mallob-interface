package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.MallobCommands;
import edu.kit.fallob.springConfig.FallobWarning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * @author Kaloyan Enev
 * @version 1.0
 * A Rest Controller for managing Mallob
 */
@RestController
@CrossOrigin
@RequestMapping("/api/v1/system/mallob")
public class MallobStartStopController {

    private static final String SYSTEM_RUNNING = "The system is already running.";

    private static final String SYSTEM_NOT_RUNNING = "The system is not running.";

    @Autowired
    private MallobCommands mallobCommands;

    /**
     * A POST endpoint for starting Mallob
     * Takes a request, parses the data needed to start Mallob and forwards it. It is also responsible for system error handling
     * @param request a MallobStartStopRequest request that contains the parameters for initializing Mallob
     * @return sends a response stating OK if successful or an error (including a status code and a message in json format)
     */
    @PostMapping("/start")
    public ResponseEntity<Object> startMallob(@RequestBody MallobStartStopRequest request) {
        return startMallobHelper(request);
    }

    /**
     * A POST endpoint for stopping Mallob
     * It is responsible for system error handling
     * @return sends a response stating OK if successful or an error (including a status code and a message in json format)
     */
    @PostMapping("/stop")
    public ResponseEntity<Object> stopMallob(){
        boolean successful = mallobCommands.stopMallob();
        if (!successful) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(SYSTEM_NOT_RUNNING);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /**
     * A POST endpoint for restarting Mallob
     * Takes a request, parses the data needed to restart Mallob and forwards it. It is also responsible for system error handling
     * @param request a MallobStartStopRequest request that contains the parameters for initializing Mallob
     * @return sends a response stating OK if successful or an error (including a status code and a message in json format)
     */
    @PostMapping("/restart")
    public ResponseEntity<Object> restartMallob(@RequestBody MallobStartStopRequest request){
        boolean successful = mallobCommands.stopMallob();
        if (!successful) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(SYSTEM_NOT_RUNNING);
        }
        return startMallobHelper(request);
    }

    /**
     * Summarizes the duplicate start Mallob part of the endpoint Methods
     * @param request the MallobStartStopRequest request that contains the parameters for initializing Mallob
     * @return a response stating OK if successful or an error (including a status code and a message in json format)
     */
    private ResponseEntity<Object> startMallobHelper(MallobStartStopRequest request) {
        boolean successful;
        try {
            successful = mallobCommands.startMallob(request.getParams());
        } catch (NullPointerException exception) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        if (!successful) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(SYSTEM_RUNNING);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
