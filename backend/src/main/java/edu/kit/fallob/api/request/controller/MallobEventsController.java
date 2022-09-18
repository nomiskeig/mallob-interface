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

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A Rest Controller for getting the Mallob Events
 */
@RestController
@CrossOrigin
@RequestMapping("/api/v1/events")
public class MallobEventsController {
    @Autowired
    private MallobCommands mallobCommands;

    /**
     * A GET endpoint for getting the Mallob events for a given time period
     * Takes a startTime and endTime parameters and parses them. It is also responsible for system error handling
     * @param startTime the starting point in time from which the events should be printed
     * @param endTime the end point in time from which the events should be printed
     * @return sends a response with the Events of the requested period or an error (including a status code and a message in json format)
     */
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

    /**
     * A GET endpoint for getting the Mallob state for a given point in time
     * Takes a time parameter and parses them. It is also responsible for system error handling
     * @param time the point in time from which the events should be printed
     * @return sends a response with the Events of the requested time point or an error (including a status code and a message in json format)
     */
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

    /**
     * Converts a String time point in to an object representing date and time
     * @param time the string that is to be converted
     * @return the time parsed in LocalDateTime object
     * @throws DateTimeParseException the time point could not be converted to an object representing the date and time
     */
    private LocalDateTime formatTime(String time) throws DateTimeParseException {
        while(!Character.isDigit(time.charAt(time.length() - 1))) {
            time = time.substring(0, time.length() - 1);
        }
        return LocalDateTime.parse(time);
    }
}
