package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.mallobio.outputupdates.Event;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A proxy for the Event class that does not contain the log line variable (for json parsing)
 */
public class EventProxy {
    //the return format for the time
    private static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

    //event-attributes
    private final int rank;
    private final int treeIndex;
    private final int jobID;
    private final int load;
    private final LocalDateTime time;


    /**
     * Constructor that takes an event and parses its attributes
     * Transforms the load boolean into (0/1) int
     * @param event the given event
     */
    public EventProxy(Event event) {
        this.rank = event.getProcessID();
        this.treeIndex = event.getTreeIndex();
        this.jobID = event.getJobID();
        if (event.isLoad()) {
            load = 1;
        } else {
            load = 0;
        }
        this.time = event.getTime();
    }


    //-----------------------------------------getter

    /**
     * Getter
     * @return 1 if the process has load
     */
    public int getLoad() {
        return load;
    }

    /**
     * Getter
     * @return job id
     */
    public int getJobID() {
        return jobID;
    }

    /**
     * Getter
     * @return tree index of the process/job
     */
    public int getTreeIndex() {
        return treeIndex;
    }

    /**
     * Getter
     * @return rank of the process
     */
    public int getRank() {
        return rank;
    }

    /**
     * Getter
     * @return the time the event happened in iso format
     */
    public String getTime() {
        //format the time to the right format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        ZonedDateTime timeWithZone = this.time.atZone(ZoneOffset.UTC);
        return timeWithZone.format(formatter);
    }

}
