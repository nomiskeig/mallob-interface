package edu.kit.fallob.api.request.controller;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import edu.kit.fallob.mallobio.outputupdates.Warning;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * a proxy for the Warning class that does not contain the logLine attribute
 * @author Valentin Schenk
 * @version 1.0
 */
public class WarningProxy {
    //describes the format in which the timestamp is returned
    private static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private final LocalDateTime timestamp;
    private final String message;


    /**
     * constructor of the class
     * @param warning the Warning object that should be converted
     */
    public WarningProxy(Warning warning) {
        this.timestamp = warning.getTime();
        this.message = warning.getMessage();
    }

    public String getTimestamp() {
        //format the time to the right format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        ZonedDateTime timeWithZone = this.timestamp.atZone(ZoneOffset.UTC);
        return timeWithZone.format(formatter);
    }

    public String getMessage() {
        return message;
    }
}
