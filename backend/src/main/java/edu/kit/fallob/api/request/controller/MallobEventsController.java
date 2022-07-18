package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.MallobCommands;
import edu.kit.fallob.dataobjects.SystemState;
import edu.kit.fallob.mallobio.outputupdates.Event;
import edu.kit.fallob.springConfig.FallobException;
import edu.kit.fallob.springConfig.FallobWarning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/events")
public class MallobEventsController {
    @Autowired
    private MallobCommands mallobCommands;

    @GetMapping("/events?startTime={startTime}&endTime={endTime}")
    public ResponseEntity<Object> getMallobUpdates(@RequestBody MallobEventsRequest request) {
        List<Event> events;
        try {
            events = mallobCommands.getEvents(request.getStartTime(), request.getEndTime());
        } catch (FallobException | NullPointerException exception) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        return ResponseEntity.ok(new MallobEventsResponse(events));
    }
    @GetMapping("/state?time={time}")
    public ResponseEntity<Object> getSystemState(@PathVariable String time) {
        SystemState state;
        try {
            state = mallobCommands.getSystemState(time);
        } catch (FallobException exception) {
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }
        return ResponseEntity.ok(new MallobEventsResponse(state.getSystemState()));
    }
}
