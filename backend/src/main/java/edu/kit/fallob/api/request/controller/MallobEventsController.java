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

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/events")
public class MallobEventsController {
    @Autowired
    private MallobCommands mallobCommands;

    @GetMapping("/events")
    public ResponseEntity<Object> getMallobUpdates(@RequestParam String startTime, @RequestParam String endTime) {
        List<Event> events;
        LocalDateTime timeLowerBound;
        LocalDateTime timeUpperBound;
        try {
            timeLowerBound = formatTime(startTime);
            timeUpperBound = formatTime(endTime);
        } catch (DateTimeParseException e) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, e.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        try {
            events = mallobCommands.getEvents(timeLowerBound, timeUpperBound);
        } catch (FallobException exception) {
            exception.printStackTrace();
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        List<EventProxy> eventProxies = new ArrayList<>();
        for (Event event : events) {
            eventProxies.add(new EventProxy(event));
        }
        return ResponseEntity.ok(new MallobEventsResponse(eventProxies));
    }

    @GetMapping("/state")
    public ResponseEntity<Object> getSystemState(@RequestParam String time) {
        SystemState state;
        LocalDateTime formattedTime;
        try {
            formattedTime = formatTime(time);
        } catch (DateTimeParseException e) {
            FallobWarning warning = new FallobWarning(HttpStatus.BAD_REQUEST, e.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        try {
            state = mallobCommands.getSystemState(formattedTime);
        } catch (FallobException exception) {
            exception.printStackTrace();
            FallobWarning warning = new FallobWarning(exception.getStatus(), exception.getMessage());
            return new ResponseEntity<>(warning, new HttpHeaders(), warning.getStatus());
        }

        List<EventProxy> eventProxies = new ArrayList<>();
        for (Event event : state.getSystemState()) {
            eventProxies.add(new EventProxy(event));
        }
        return ResponseEntity.ok(new MallobEventsResponse(eventProxies));
    }

    private LocalDateTime formatTime(String time) throws DateTimeParseException {
        while(!Character.isDigit(time.charAt(time.length() - 1))) {
            time = time.substring(0, time.length() - 1);
        }
        return LocalDateTime.parse(time);
    }
}
