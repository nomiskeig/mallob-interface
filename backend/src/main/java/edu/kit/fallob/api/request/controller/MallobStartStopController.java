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
    public ResponseEntity<Object> startMallob(@RequestParam String params) {
        boolean successful = mallobCommands.startMallob(params);
        if (!successful) {
            return ResponseEntity.ok(HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @PostMapping("/stop")
    public ResponseEntity<Object> stopMallob(){
        boolean successful = mallobCommands.stopMallob();
        if (!successful) {
            return ResponseEntity.ok(HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @PostMapping("/restart")
    public ResponseEntity<Object> restartMallob(@RequestParam String params){
        boolean successful = mallobCommands.startMallob(params);
        if (!successful) {
            return ResponseEntity.ok(HttpStatus.CONFLICT);
        }
        successful = mallobCommands.stopMallob();
        if (!successful) {
            return ResponseEntity.ok(HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
