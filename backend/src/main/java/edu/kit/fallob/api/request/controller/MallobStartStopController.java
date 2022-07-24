package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.MallobCommands;
import org.springframework.beans.factory.annotation.Autowired;
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
        boolean successful = mallobCommands.startMallob(request.getParams());
        if (!successful) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The system is already running");
        }
        return ResponseEntity.ok(HttpStatus.OK);
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
        successful = mallobCommands.startMallob(request.getParams());
        if (!successful) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The system is already running");
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
