package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.MallobCommands;
import edu.kit.fallob.springConfig.FallobWarning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("/api/v1/system/mallob")
public class MallobStartStopController {

    @Autowired
    private MallobCommands mallobCommands;

    @PostMapping("/start")
    public ResponseEntity<Object> startMallob(@RequestBody MallobStartStopRequest request) {
        return startMallobHelper(request);
    }
    @PostMapping("/stop")
    public ResponseEntity<Object> stopMallob(){
        boolean successful = mallobCommands.stopMallob();
        if (!successful) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The system is not running");
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @PostMapping("/restart")
    public ResponseEntity<Object> restartMallob(@RequestBody MallobStartStopRequest request){
        boolean successful = mallobCommands.stopMallob();
        if (!successful) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The system is not running");
        }
        return startMallobHelper(request);
    }

    private ResponseEntity<Object> startMallobHelper(@RequestBody MallobStartStopRequest request) {
        boolean successful;
        try {
            successful = mallobCommands.startMallob(request.getParams());
        } catch (NullPointerException exception) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        if (!successful) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The system is already running");
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
